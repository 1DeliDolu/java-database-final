package com.project.code.Service;

import com.project.code.Model.Customer;
import com.project.code.Model.Inventory;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.OrderItem;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Product;
import com.project.code.Model.PurchaseProductDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderDetailsRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Repo.StoreRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {
        if (placeOrderRequest == null || placeOrderRequest.getStoreId() == null || placeOrderRequest.getStoreId() <= 0) {
            throw new RuntimeException("Invalid order request");
        }
        if (placeOrderRequest.getPurchaseProduct() == null || placeOrderRequest.getPurchaseProduct().isEmpty()) {
            throw new RuntimeException("Order must contain at least one product");
        }

        // 1. Retrieve or create the Customer
        Customer existingCustomer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        Customer customer = new Customer();
        customer.setName(placeOrderRequest.getCustomerName());
        customer.setEmail(placeOrderRequest.getCustomerEmail());
        customer.setPhone(placeOrderRequest.getCustomerPhone());

        if (existingCustomer == null) {
            customer = customerRepository.save(customer);
        }
        else{
            customer=existingCustomer;
        }


        // 2. Retrieve the Store
        Store store = storeRepository.findById(placeOrderRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // 3. Create OrderDetails
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setTotalPrice(placeOrderRequest.getTotalPrice());
        orderDetails.setDate(java.time.LocalDateTime.now()); // Use current datetime

        orderDetails = orderDetailsRepository.save(orderDetails); 

        // 4. Create and save OrderItems (products purchased)
        List<PurchaseProductDTO> purchaseProducts = placeOrderRequest.getPurchaseProduct();
        for (PurchaseProductDTO purchaseProduct : purchaseProducts) {
            if (purchaseProduct == null || purchaseProduct.getId() == null || purchaseProduct.getId() <= 0) {
                throw new RuntimeException("Invalid product in order");
            }
            if (purchaseProduct.getQuantity() == null || purchaseProduct.getQuantity() <= 0) {
                throw new RuntimeException("Invalid quantity for product: " + purchaseProduct.getId());
            }

            OrderItem orderItem = new OrderItem();

            Inventory inventory=inventoryRepository.findByProductIdandStoreId(purchaseProduct.getId(),placeOrderRequest.getStoreId());
            if (inventory == null) {
                throw new RuntimeException("Inventory not found for product: " + purchaseProduct.getId());
            }
            if (inventory.getStockLevel() == null || inventory.getStockLevel() < purchaseProduct.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + purchaseProduct.getId());
            }

            inventory.setStockLevel(inventory.getStockLevel()-purchaseProduct.getQuantity());
            inventoryRepository.save(inventory);

            orderItem.setOrder(orderDetails); // Link the order to the order item

            Product product = productRepository.findByid(purchaseProduct.getId());
            if (product == null) {
                throw new RuntimeException("Product not found: " + purchaseProduct.getId());
            }
            orderItem.setProduct(product);

            orderItem.setQuantity(purchaseProduct.getQuantity());
            Double unitPrice = purchaseProduct.getPrice();
            if (unitPrice == null || unitPrice < 0) {
                throw new RuntimeException("Invalid price for product: " + purchaseProduct.getId());
            }
            orderItem.setPrice(unitPrice * purchaseProduct.getQuantity());

            orderItemRepository.save(orderItem); // Save OrderItem
        }
    }
}
