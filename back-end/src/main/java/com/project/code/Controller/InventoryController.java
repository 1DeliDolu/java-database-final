package com.project.code.Controller;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ServiceClass serviceClass;

    public InventoryController(ProductRepository productRepository,
                               InventoryRepository inventoryRepository,
                               ServiceClass serviceClass) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.serviceClass = serviceClass;
    }

    @PutMapping
    public Map<String, String> updateInventory(@RequestBody CombinedRequest combinedRequest) {
        Map<String, String> response = new HashMap<>();

        try {
            if (combinedRequest == null) {
                response.put("message", "Invalid request data");
                return response;
            }

            Product product = combinedRequest.getProduct();
            Inventory inventory = combinedRequest.getInventory();

            if (product == null || inventory == null || product.getId() <= 0) {
                response.put("message", "Invalid request data");
                return response;
            }
            if (inventory.getProduct() == null || inventory.getStore() == null
                    || inventory.getProduct().getId() <= 0
                    || inventory.getStore().getId() == null || inventory.getStore().getId() <= 0
                    || inventory.getStockLevel() == null || inventory.getStockLevel() < 0) {
                response.put("message", "Invalid inventory data");
                return response;
            }
            if (inventory.getProduct().getId() != product.getId()) {
                response.put("message", "Product id mismatch");
                return response;
            }

            if (!serviceClass.ValidateProductId(product.getId())) {
                response.put("message", "Product not present in database");
                return response;
            }

            productRepository.save(product);
            Inventory existingInventory = serviceClass.getInventoryId(inventory);

            if (existingInventory == null) {
                response.put("message", "No data available");
                return response;
            }

            existingInventory.setStockLevel(inventory.getStockLevel());
            existingInventory.setProduct(product);
            existingInventory.setStore(inventory.getStore());
            inventoryRepository.save(existingInventory);
            response.put("message", "Successfully updated product");
            return response;
        } catch (DataIntegrityViolationException ex) {
            response.put("Error", "Data integrity violation while updating inventory");
            return response;
        } catch (Exception ex) {
            response.put("Error", ex.getMessage());
            return response;
        }
    }

    @PostMapping
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        Map<String, String> response = new HashMap<>();

        try {
            if (inventory == null || inventory.getProduct() == null || inventory.getStore() == null
                    || inventory.getProduct().getId() <= 0
                    || inventory.getStore().getId() == null || inventory.getStore().getId() <= 0
                    || inventory.getStockLevel() == null || inventory.getStockLevel() < 0) {
                response.put("message", "Invalid inventory data");
                return response;
            }

            if (!serviceClass.validateInventory(inventory)) {
                response.put("message", "Data already present");
                return response;
            }

            inventoryRepository.save(inventory);
            response.put("message", "Data saved successfully");
            return response;
        } catch (DataIntegrityViolationException ex) {
            response.put("Error", "Data integrity violation while saving inventory");
            return response;
        } catch (Exception ex) {
            response.put("Error", ex.getMessage());
            return response;
        }
    }

    @GetMapping("/{storeid}")
    public Map<String, Object> getAllProducts(@PathVariable Long storeid) {
        Map<String, Object> response = new HashMap<>();
        response.put("products", productRepository.findProductsByStoreId(storeid));
        return response;
    }

    @GetMapping("/filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName(@PathVariable String category,
                                              @PathVariable String name,
                                              @PathVariable Long storeid) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if ("null".equalsIgnoreCase(category)) {
            products = productRepository.findByNameLike(storeid, name);
        } else if ("null".equalsIgnoreCase(name)) {
            products = productRepository.findByCategoryAndStoreId(storeid, category);
        } else {
            products = productRepository.findByNameAndCategory(storeid, name, category);
        }

        response.put("product", products);
        return response;
    }

    @GetMapping("/search/{name}/{storeId}")
    public Map<String, Object> searchProduct(@PathVariable String name, @PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        response.put("product", productRepository.findByNameLike(storeId, name));
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        try {
            if (!serviceClass.ValidateProductId(id)) {
                response.put("message", "Product not present in database");
                return response;
            }

            inventoryRepository.deleteByProductId(id);
            productRepository.deleteById(id);
            response.put("message", "Product deleted successfully");
            return response;
        } catch (Exception ex) {
            response.put("Error", ex.getMessage());
            return response;
        }
    }

    @GetMapping("/validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable Integer quantity,
                                    @PathVariable Long storeId,
                                    @PathVariable Long productId) {
        if (quantity == null || quantity <= 0 || storeId == null || storeId <= 0 || productId == null || productId <= 0) {
            return false;
        }
        Inventory inventory = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        return inventory != null && inventory.getStockLevel() != null && inventory.getStockLevel() >= quantity;
    }
}
