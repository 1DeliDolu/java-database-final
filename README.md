#InventoryController.java 
package com.project.code.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass serviceClass;

    @PutMapping
    public Map<String, String> updateInventory(@RequestBody CombinedRequest request) {
        Product product = request.getProduct();
        Inventory inventory = request.getInventory();

        Map<String, String> map = new HashMap<>();
        System.out.println("Stock Level: " + inventory.getStockLevel());
        if (!serviceClass.ValidateProductId(product.getId())) {
            map.put("message", "Id " + product.getId() + " not present in database");
            return map;
        }
        productRepository.save(product);
        map.put("message", "Successfully updated product with id: " + product.getId());

        if (inventory != null) {
            try {
                Inventory result = serviceClass.getInventoryId(inventory);
                if (result != null) {
                    inventory.setId(result.getId());
                    inventoryRepository.save(inventory);
                } else {
                    map.put("message", "No data available for this product or store id");
                    return map;
                }

            } catch (DataIntegrityViolationException e) {
                map.put("message", "Error: " + e);
                System.out.println(e);
                return map;
            } catch (Exception e) {
                map.put("message", "Error: " + e);
                System.out.println(e);
                return map;
            }
        }

        return map;

    }

    @PostMapping
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {

        Map<String, String> map = new HashMap<>();
        try {
            if (serviceClass.validateInventory(inventory)) {
                inventoryRepository.save(inventory);
            } else {
                map.put("message", "Data Already present in inventory");
                return map;
            }

        } catch (DataIntegrityViolationException e) {
            map.put("message", "Error: " + e);
            System.out.println(e);
            return map;
        } catch (Exception e) {
            map.put("message", "Error: " + e);
            System.out.println(e);
            return map;
        }
        map.put("message", "Product added to inventory successfully");
        return map;
    }

    @GetMapping("/{storeid}")
    public Map<String, Object> getAllProducts(@PathVariable Long storeid) {
        Map<String, Object> map = new HashMap<>();
        List<Product> result = productRepository.findProductsByStoreId(storeid);
        map.put("products", result);
        return map;
    }

    @GetMapping("filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName(@PathVariable String category, @PathVariable String name,
            @PathVariable long storeid) {
        Map<String, Object> map = new HashMap<>();
        if (category.equals("null") ) {
            map.put("product", productRepository.findByNameLike(storeid, name));
            return map;
        }
        else if(name.equals("null"))
        {
            System.out.println("name is null");
            map.put("product", productRepository.findByCategoryAndStoreId(storeid,category));
            return map;
        }
        map.put("product", productRepository.findByNameAndCategory(storeid, name, category));
        return map;
    }

    @GetMapping("search/{name}/{storeId}")
    public Map<String,Object> searchProduct(@PathVariable String name, @PathVariable long storeId)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("product", productRepository.findByNameLike(storeId, name));
        return map;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable Long id) {
        Map<String, String> map = new HashMap<>();

        if (!serviceClass.ValidateProductId(id)) {
            map.put("message", "Id " + id + " not present in database");
            return map;
        }
        inventoryRepository.deleteByProductId(id);
        map.put("message", "Deleted product successfully with id: " + id);
        return map;
    }

    @GetMapping("validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable int quantity, @PathVariable long storeId,
            @PathVariable long productId) {
        Inventory result = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        if (result.getStockLevel() >= quantity) {
            return true;
        }
        return false;

    }

}

ProductController.java 
package com.project.code.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@RequestMapping("/product")
@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ServiceClass serviceClass;

    @Autowired
    private InventoryRepository inventoryRepository;

    @PostMapping
    public Map<String, String> addProduct(@RequestBody Product product) {

        Map<String, String> map = new HashMap<>();
        if (!serviceClass.validateProduct(product)) {
            map.put("message", "Product already present in database");
            return map;
        }
        try {
            productRepository.save(product);
            map.put("message", "Product added successfully");
        }

        catch (DataIntegrityViolationException e) {
            map.put("message", "SKU should be unique");
        }
        return map;
    }

    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId(@PathVariable Long id) {
        System.out.println("result: ");
        System.out.println("result: ");
        System.out.println("result: ");
        Map<String, Object> map = new HashMap<>();
        Product result = productRepository.findByid(id);

        System.out.println("result: "+result);
        map.put("products", result);
        return map;
    }

    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {
        Map<String, String> map = new HashMap<>();
        try {
            productRepository.save(product);
            map.put("message", "Data upated sucessfully");
        } catch (Error e) {
            map.put("message", "Error occured");
        }

        return map;
    }

    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(@PathVariable String name,@PathVariable String category) {
        Map<String, Object> map = new HashMap<>();

        if(name.equals("null"))
        {
            map.put("products", productRepository.findByCategory(category));
            return map;
        }
        else if(category.equals("null"))
        {
            map.put("products", productRepository.findProductBySubName(name));
            return map;

        }
        map.put("products",productRepository.findProductBySubNameAndCategory(name,category));
        return map;

    }

    @GetMapping
    public Map<String, Object> listProduct() {

        Map<String, Object> map = new HashMap<>();
        map.put("products",productRepository.findAll());
        return map;
    }  




    @GetMapping("filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId(@PathVariable String category,@PathVariable long storeid) {
       Map<String, Object> map = new HashMap<>();
       List<Product> result = productRepository.findProductByCategory(category,storeid);

        map.put("product", result);
        return map;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id) {
        Map<String, String> map = new HashMap<>();

        if (!serviceClass.ValidateProductId(id)) {
            map.put("message", "Id " + id + " not present in database");
            return map;
        }
        inventoryRepository.deleteByProductId(id);
        orderItemRepository.deleteByProductId(id);
        productRepository.deleteById(id);

        map.put("message", "Deleted product successfully with id: " + id);
        return map;
    }

    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("products", productRepository.findProductBySubName(name));
        return map;
    }


}

#ReviewController.java 
package com.project.code.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.Customer;
import com.project.code.Model.Review;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.ReviewRepository;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    CustomerRepository customerRepository;

    @GetMapping("/{storeId}/{productId}")
    public Map<String,Object> getReviews(@PathVariable long storeId, @PathVariable long productId)
    {
        Map<String, Object> map = new HashMap<>();
         List<Review> reviews = reviewRepository.findByStoreIdAndProductId(storeId,productId);

         List<Map<String, Object>> reviewsWithCustomerNames = new ArrayList<>();

         // For each review, fetch customer details and add them to the response
         for (Review review : reviews) {
             Map<String, Object> reviewMap = new HashMap<>();
             reviewMap.put("review", review.getComment());
             reviewMap.put("rating", review.getRating());

             // Fetch customer details using customerId
             Customer customer = customerRepository.findByid(review.getCustomerId());
             if (customer != null) {
                 reviewMap.put("customerName", customer.getName());  
             } else {
                 reviewMap.put("customerName", "Unknown");
             }

             reviewsWithCustomerNames.add(reviewMap);
         }

         map.put("reviews", reviewsWithCustomerNames);
         return map;

    }

    @GetMapping
    public Map<String,Object> getAllReviews()
    {
        Map<String,Object> map=new HashMap<>();
        map.put("reviews",reviewRepository.findAll());
        return map;
    }


}

# StoreController.java
package com.project.code.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.StoreRepository;
import com.project.code.Service.OrderService;


@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Map<String, String> addStore(@RequestBody Store store) {
        Store savedStore = storeRepository.save(store);
        Map<String, String> map = new HashMap<>();
        map.put("message", "Store added successfully with id "+ savedStore.getId());
        return map;
    }


    @GetMapping("validate/{storeId}")
    public boolean validateStore(@PathVariable Long storeId ) 
    {
        Store store=storeRepository.findByid(storeId);
        if(store!=null)
        {
            return true;
        }
        return false;
    }


    @PostMapping("/placeOrder")
    public Map<String,String> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequest) {

        Map<String,String> map=new HashMap<>();
        try{
        orderService.saveOrder(placeOrderRequest);
        map.put("message","Order placed successfully");
        }
        catch(Error e)
        {
            map.put("Error",""+e);

        }
        return map;  
    }

}