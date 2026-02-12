Part 4: Create Controllers
Controllers handle incoming HTTP requests, map them to appropriate service methods, and return responses to the client. They act as the entry point of the application, managing the flow between the frontend and backend while keeping the business logic separate.

Inventory Controller
Purpose: This controller handles the CRUD operations for managing inventory. It provides endpoints for updating, saving, searching, and validating inventory and products.

Open the InventoryController.java file
 Open InventoryController.java in IDE

Set Up the Controller Class
Annotate the class with @RestController to designate it as a REST controller for handling HTTP requests.

Use @RequestMapping("/inventory") to set the base URL path for all methods in this controller.

Autowired Dependencies
Autowire the necessary services and repositories:

ProductRepository for accessing the product data.

InventoryRepository for accessing inventory data.

ServiceClass for validating product IDs and inventory.

Define the updateInventory Method
Annotate this method with @PutMapping.

The method should accept a request body of type CombinedRequest, which contains both a Product and Inventory.

It should return a Map<String, String>.

Validate the product ID.
Hint: Use method created in ServiceClass to validate productId. If the product doesn't exist, return an error message.

Update the Product and Inventory if the product ID is valid:

If the Inventory exists for the product, update it and save the changes also return a key message with value “Successfully updated product”

If the Inventory doesn't exist for the product, return a key message with value “No data available”.

Catch any exceptions (e.g., DataIntegrityViolationException) and handle them appropriately.

Define the saveInventory Method
Annotate this method with @PostMapping.

The method should accept an Inventory object in the request body.

The method should return Map<String, String>

Check if the inventory already exists.
Hint: Use the method validateInventory(inventory) created in ServiceClass to validate inventory.

If it exists, return a message saying that the data is already present.

If it doesn't exist, save the Inventory object to the repository and return message saying data saved successfully

Catch any exceptions related to data integrity or other errors and handle them appropriately.

Define the getAllProducts Method
Annotate this method with @GetMapping("/{storeid}") to retrieve products for a specific store.

The method should accept a storeId as a path variable and fetch the products for that store.
Hint: Call the findProductsByStoreId(storeid) method on the ProductRepository to get the products belonging to the store with the given storeid.

The method should return Map<String, Object>

Return the list of products as part of a map with the key "products".

Define the getProductName Method
Annotate this method with @GetMapping("filter/{category}/{name}/{storeid}").

This method should filter products by category and name.

The method should return Map<String, Object>

If either category or name is "null", adjust the filtering logic accordingly:

If category is "null", filter by product name only.
Hint: Call the findByNameLike(storeid, name) method on the ProductRepository to get the filtered products by name.

If name is "null", filter by category only.
Hint: Call the findByCategoryAndStoreId method on the ProductRepository to get the filtered products by category.

If both category and name are provided, filter products by both parameters.
Hint: Call the findByNameAndCategory method on the ProductRepository to get the filtered products by name and category.

Return the filtered products as part of the response map with the key "product".

Define the searchProduct Method
Annotate this method with @GetMapping("search/{name}/{storeId}").

The method should return Map<String, Object>

The method should search for products by name within a specific store.

Hint: Use method findByNameLike(storeId, name) on the ProductRepository to search for products with names that match the name parameter.

Return the products found in the response map with the key "product".

Define the removeProduct Method
Annotate this method with @DeleteMapping("/{id}").

The method should return Map<String, String>

This method should delete a product by its id.

Hint: Use method ValidateProductId(id) on the ServiceClass to check if the product exists. If it doesn't exist, return a message saying the product not present in database.

If the product exists, delete the corresponding Inventory entry

Hint: Use method deleteByProductId(id) on the InventoryRepository.

Return a success message with key messageindicating that the product was deleted.

Define the validateQuantity Method
Annotate this method with @GetMapping("validate/{quantity}/{storeId}/{productId}").

The method should return boolean .

This method should validate if a specified quantity of a product is available in stock at a given store.

Retrieve the inventory for the product and store.

Hint: Use method findByProductIdandStoreId(productId, storeId) on the InventoryRepository.

If the stock level is greater than or equal to the requested quantity, return true. Otherwise, return false.

Product Controller
Purpose: This controller manages the CRUD operations related to the Product entity in your application. Here's how to implement it step by step.

Open the ProductController.java file
 Open ProductController.java in IDE

Set Up the Controller Class
Annotate the class with @RestController to denote it as a controller that handles REST API calls.

Map the class to the /product URL by using @RequestMapping("/product").

Autowired Dependencies
You need the following dependencies injected via @Autowired:

ProductRepository: For interacting with the product data in the database.

ServiceClass: For validating product data and checking business logic like product existence.

InventoryRepository: To manage the inventory associated with the products.

Define the addProduct Method
@PostMapping: This method will handle POST requests to add a new product.

This method will return Map<String, String>

Request Body: Accept a Product object from the request body.

Validation: Check if the product already exists.

Hint: Use method validateProduct() on the ServiceClass.

Save Product: If the product is valid, save it to the database.

Hint: Use save() method of ProductRepository to save.

After saving the product return a success message with key message.

Handle Errors: Catch exceptions such as DataIntegrityViolationException for scenarios like unique SKU violations.

Define the getProductbyId Method
@GetMapping("/product/{id}"): This method will handle GET requests to retrieve a product by its id.

This method will return Map<String, Object>

Path Variable: Use @PathVariable to accept the product id in the URL.

Return Product: Fetch the product and return it in a map with key products.

Hint: Use method findById(id) of ProductRepository to fetch product.

Define the updateProduct Method
@PutMapping: This method will handle PUT requests to update an existing product.

This method will return Map<String, String>

Request Body: Accept a Product object to update.

Save Product: Save the updated product.

Hint: Use save() method of ProductRepository to save the updated Product.

After updating the product return a success message with key message.

Error Handling: Catch any errors during the save operation.

Define the filterbyCategoryProduct Method
@GetMapping("/category/{name}/{category}"): This method filters products based on their name and category.

This method will return Map<String, Object>

Handle Null Parameters: If name or category is "null", apply conditional filtering logic accordingly.

Return Filtered Products: Fetch products using repository methods like findByCategory(), findByCategory(category) or findProductBySubNameAndCategory() based on the provided filters and return in with key products

Define the listProduct Method
@GetMapping: This method will handle GET requests to retrieve all products.

This method will return Map<String, Object>

Return All Products: Fetch and return all products with key products

Hint: Use findAll() method of ProductRepository to fetch all the product.

Define the getProductbyCategoryAndStoreId Method
@GetMapping("filter/{category}/{storeid}"): This method will filter products by category and storeId.

This method will return Map<String, Object>

Fetch Products: Retrieve all the products by category & storeId and return with key product

Hint: Use method findProductByCategory() of ProductRepository. to retrieve all the products by category and storeId

Define the deleteProduct Method
@DeleteMapping("/{id}"): This method will handle DELETE requests to remove a product by its id.

This method will return Map<String, String>

Validation: Check if the product exists before deleting.

Hint: Use ValidateProductId() method of ServiceClass to validate product.

Delete Product: As Inventory is mapped with Product using foreign key constraint, delete from Inventory table first and then from Product table.

Hint: Use method deleteByProductId(id) of inventoryRepository to remove the inventory entry and use method deleteById(id) of productRepository to delete the product.

Return a success message with key messageindicating that the product was deleted.

Define the searchProduct Method
@GetMapping("/searchProduct/{name}"): This method will search for products by their name.

This method will return Map<String, Object>

Return Search Results: Search for products by name an return with key products

Hint: Use method findProductBySubName() of ProductRepositoryto search products.

Review Controller
Purpose: The ReviewController handles endpoints for retrieving reviews for products in a store. It provides methods for getting all reviews or filtered reviews by store ID and product ID, with customer information associated with each review.

Open the ReviewController.java file
 Open ReviewController.java in IDE

Set Up the Controller Class
Action: Annotate the class with @RestController to designate it as a REST controller for handling HTTP requests.

URL Mapping: Use @RequestMapping("/reviews") to define the base URL for all methods in this controller.

Autowired Dependencies
Action: Autowire the necessary repositories:

ReviewRepository for accessing the review data.

CustomerRepository for retrieving customer details linked with reviews.

Define the getReviews Method
URL Mapping: Use @GetMapping("/{storeId}/{productId}") to create an endpoint that retrieves reviews for a specific product in a store by storeId and productId.

Method will return a Map<String, Object>

Path Variables:

storeId: The ID of the store.

productId: The ID of the product.

Logic :

First retreive all the reviews from the database of a specific product of a store.
Hint: Use method findByStoreIdAndProductId of ReviewRepository to fetch all the reviews of a specific product of a store.
Now from all the recevied reviews filter remove the unwanted data and keep comment, rating. Now add name of the customer to the table using customer id field in the reviews.
Hint: Use method findByid(review.getCustomerId()) of CustomerRepository
Return Key: The response will include a key named reviews. The value for this key will be a list of review objects, each containing the review comment, rating, and the name of the customer who wrote the review.

Customer Name Key: Each review object will include a key named customerName, which will either contain the customer's name or "Unknown" if no customer is found.
Store Controller
Purpose: The StoreController handles the operations related to stores, including adding a new store, validating an existing store, and placing an order. It integrates with the StoreRepository and the OrderService for managing store and order operations.

Open the StoreController.java file
 Open StoreController.java in IDE

Set Up the Controller Class
Action: Annotate the class with @RestController to designate it as a REST controller for handling HTTP requests.

URL Mapping: Use @RequestMapping("/store") to set the base URL path for all methods in this controller.

Autowired Dependencies
Action: Autowire the necessary repositories and services:

StoreRepository for accessing store data.

OrderService for handling order-related functionality.

Define the addStore Method
URL Mapping: Use @PostMapping to create an endpoint for adding a new store.

Request Body: This method should accept a Store object in the request body.

Return Key: The response will include a key named message. The value for this key will indicate that the store was successfully created and will include the store's ID.

Define the validateStore Method
URL Mapping: Use @GetMapping("validate/{storeId}") to create an endpoint that checks if a store with a given storeId exists.

Path Variable: storeId represents the ID of the store to be validated.

Return Key: This method will return a boolean value indicating whether the store exists. If the store is found, the method will return true; otherwise, it will return false.

Define the placeOrder Method
URL Mapping: Use @PostMapping("/placeOrder") to create an endpoint for placing an order.

Request Body: This method should accept a PlaceOrderRequestDTO object in the request body.

Return Key: The response will include a key named message with the value "Order placed successfully" if the order is successfully processed.

Error Key: If an error occurs while placing the order, the response will include a key named Error with the value being the error message.
Response Structure for Methods
addStore: Returns a Map<String, String> with a key named message indicating the store ID.

validateStore: Returns a boolean value to indicate if the store exists (true or false).

placeOrder: Returns a Map<String, String> with either:

message: "Order placed successfully" if the order is placed without issues.

Error: The error message if there is a failure in processing the order.

Global Exception Handler
Purpose: The GlobalExceptionHandler is responsible for handling exceptions globally across all controllers. It ensures that the application responds with meaningful error messages when an exception occurs, improving the user experience and maintaining consistent error handling.

Open the GlobalExceptionHandler.java file
 Open GlobalExceptionHandler.java in IDE

Set Up the Global Exception Handler Class
Action: Annotate the class with @RestControllerAdvice. This annotation makes the class capable of handling exceptions globally for all REST controllers in your application.
Define the handleJsonParseException Method
Exception Type: Use the @ExceptionHandler(HttpMessageNotReadableException.class) annotation to handle HttpMessageNotReadableException. This exception typically occurs when the request body is not formatted correctly (e.g., invalid JSON syntax).

HTTP Status: Use @ResponseStatus(HttpStatus.BAD_REQUEST) to specify that the response will have an HTTP status of 400 Bad Request when this exception is thrown.

Return Key: The method will return a Map<String, Object> containing the following key:

message: A descriptive error message indicating that the input provided is invalid. The value of the message key should be: "Invalid input: The data provided is not valid."
Exception Handling Flow
If a request fails to parse correctly (e.g., due to invalid JSON), the application will invoke the handleJsonParseException method.

This will return a response with a 400 Bad Request status and the error message under the key message.


