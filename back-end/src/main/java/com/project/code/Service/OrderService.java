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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(ProductRepository productRepository,
                        InventoryRepository inventoryRepository,
                        CustomerRepository customerRepository,
                        StoreRepository storeRepository,
                        OrderDetailsRepository orderDetailsRepository,
                        OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.customerRepository = customerRepository;
        this.storeRepository = storeRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        if (customer == null) {
            customer = new Customer(
                    placeOrderRequest.getCustomerName(),
                    placeOrderRequest.getCustomerEmail(),
                    placeOrderRequest.getCustomerPhone()
            );
            customer = customerRepository.save(customer);
        }

        Store store = storeRepository.findById(placeOrderRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found: " + placeOrderRequest.getStoreId()));

        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setTotalPrice(placeOrderRequest.getTotalPrice());
        orderDetails.setDate(LocalDateTime.now());
        orderDetails = orderDetailsRepository.save(orderDetails);

        if (placeOrderRequest.getPurchaseProduct() == null) {
            return;
        }

        for (PurchaseProductDTO purchaseProduct : placeOrderRequest.getPurchaseProduct()) {
            Product product = productRepository.findById(purchaseProduct.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + purchaseProduct.getId()));

            Inventory inventory = inventoryRepository.findByProductIdandStoreId(product.getId(), store.getId());
            if (inventory == null) {
                throw new RuntimeException("Inventory not found for product " + product.getId() + " at store " + store.getId());
            }

            Integer currentStock = inventory.getStockLevel() == null ? 0 : inventory.getStockLevel();
            Integer requestedQty = purchaseProduct.getQuantity() == null ? 0 : purchaseProduct.getQuantity();
            if (requestedQty > currentStock) {
                throw new RuntimeException("Insufficient stock for product: " + product.getId());
            }

            inventory.setStockLevel(currentStock - requestedQty);
            inventoryRepository.save(inventory);

            OrderItem orderItem = new OrderItem(orderDetails, product, requestedQty, purchaseProduct.getPrice());
            orderItemRepository.save(orderItem);
        }
    }
}
