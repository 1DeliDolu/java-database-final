package com.project.code.Controller;

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
@RequestMapping("/product")
public class ProductController {
    private final ProductRepository productRepository;
    private final ServiceClass serviceClass;
    private final InventoryRepository inventoryRepository;

    public ProductController(ProductRepository productRepository,
                             ServiceClass serviceClass,
                             InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.serviceClass = serviceClass;
        this.inventoryRepository = inventoryRepository;
    }

    @PostMapping
    public Map<String, String> addProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();

        try {
            if (!isValidProductInput(product, false)) {
                response.put("message", "Invalid product data");
                return response;
            }

            if (!serviceClass.validateProduct(product)) {
                response.put("message", "Product already exists");
                return response;
            }

            productRepository.save(product);
            response.put("message", "Product added successfully");
            return response;
        } catch (DataIntegrityViolationException ex) {
            response.put("Error", "Data integrity violation while saving product");
            return response;
        } catch (Exception ex) {
            response.put("Error", ex.getMessage());
            return response;
        }
    }

    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("products", productRepository.findById(id).orElse(null));
        return response;
    }

    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();

        try {
            if (!isValidProductInput(product, true)) {
                response.put("message", "Invalid product data");
                return response;
            }
            if (!serviceClass.ValidateProductId(product.getId())) {
                response.put("message", "Product not present in database");
                return response;
            }

            productRepository.save(product);
            response.put("message", "Product updated successfully");
            return response;
        } catch (Exception ex) {
            response.put("Error", ex.getMessage());
            return response;
        }
    }

    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(@PathVariable String name, @PathVariable String category) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if ("null".equalsIgnoreCase(name)) {
            products = productRepository.findByCategory(category);
        } else if ("null".equalsIgnoreCase(category)) {
            products = productRepository.findProductBySubName(name);
        } else {
            products = productRepository.findProductBySubNameAndCategory(name, category);
        }

        response.put("products", products);
        return response;
    }

    @GetMapping
    public Map<String, Object> listProduct() {
        Map<String, Object> response = new HashMap<>();
        response.put("products", productRepository.findAll());
        return response;
    }

    @GetMapping("/filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId(@PathVariable String category, @PathVariable Long storeid) {
        Map<String, Object> response = new HashMap<>();
        response.put("product", productRepository.findProductByCategory(category, storeid));
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id) {
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

    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("products", productRepository.findProductBySubName(name));
        return response;
    }

    private boolean isValidProductInput(Product product, boolean requireId) {
        if (product == null) {
            return false;
        }
        if (requireId && product.getId() <= 0) {
            return false;
        }
        return product.getName() != null && !product.getName().trim().isEmpty()
                && product.getCategory() != null && !product.getCategory().trim().isEmpty()
                && product.getSku() != null && !product.getSku().trim().isEmpty()
                && product.getPrice() != null && product.getPrice() >= 0;
    }
}
