OrderDetails Repository
Open the OrderDetailsRepository.java file
 Open OrderDetailsRepository.java in IDE

Create a repository for the OrderDetails model by extending JpaRepository. This will allow for basic CRUD operations without needing to implement the methods manually.
Hint:

Extend JpaRepository<OrderDetails, Long> to inherit basic CRUD functionality.

No custom methods are required for this repository as it handles basic operations for the OrderDetails model.

OrderItem Repository
Open the OrderItemRepository.java file
 Open OrderItemRepository.java in IDE

Create a repository for the OrderItem model by extending JpaRepository. This will allow for basic CRUD operations without needing to implement the methods manually.
Hint:

Extend JpaRepository<OrderItem, Long> to inherit basic CRUD functionality.

No custom methods are required for this repository as it handles basic operations for the OrderItem model.

Product Repository
Open the ProductRepository.java file
 Open ProductRepository.java in IDE

Create a repository for the Product model by extending JpaRepository. This will allow for basic CRUD operations and custom queries.
Add the following methods:
findAll: Find all products.

Return type: List<Product>
Hint: Use the built-in findAll method from JpaRepository.

findByCategory: Find products by their category.

Return type: List<Product>

Parameter: String category

Hint: Use the findBy convention with category.

findByPriceBetween: Find products within a price range.

Return type: List<Product>

Parameters: Double minPrice, Double maxPrice

Hint: Use the findBy convention with priceBetween.

findBySku: Find a product by its SKU.

Return type: List<Product>

Parameter: String sku

Hint: Use the findBy convention with sku.

findByName: Find a product by its name.

Return type: Product

Parameter: String name

Hint: Use the findBy convention with name.

findById: Find a product by its ID.

Return type: Product

Parameter: Long id

Hint: Use the findBy convention with id.

findByNameLike: Find products by a name pattern for a specific store.

Return type: List<Product>

Parameters: Long storeId, String pname

Use @Query annotation and write the following query

@Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND i.product.category = :category")

findByNameAndCategory: Find products by name and category for a specific store.

Return type: List<Product>

Parameters: Long storeId, String pname, String category

Use @Query annotation and write the following query

@Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%')) AND i.product.category = :category") to combine name and category filters.

findByCategoryAndStoreId: Find products by category for a specific store.

Return type: List<Product>

Parameters: Long storeId, String category

Use @Query annotation and write the following query @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND i.product.category = :category") to filter by both category and storeId.

findProductBySubName: Find products by a name pattern (ignoring case).

Return type: List<Product>

Parameter: String pname

Use @Query annotation and write the following query @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%'))") for partial matching in name.

findProductsByStoreId: Find all products for a specific store.

Return type: List<Product>

Parameter: Long storeId

Use @Query annotation and write the following query @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId") to join Inventory and filter by storeId.

findProductByCategory: Find products by category for a specific store.

Return type: List<Product>

Parameters: String category, Long storeId

Use @Query annotation and write the following query @Query("SELECT i.product FROM Inventory i WHERE i.product.category = :category and i.store.id = :storeId") and filter by storeId. to filter by category and storeId.

findProductBySubNameAndCategory: Find products by a name pattern and category.

Return type: List<Product>

Parameters: String pname, String category

Use @Query annotation and write the following query @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%')) AND i.category = :category") to match both name and category criteria.

Hint:

Extend JpaRepository<Product, Long> to inherit basic CRUD functionality.

Use @Query for more complex queries that involve custom conditions.

Use LOWER in queries to make case-insensitive searches.

Use CONCAT for partial matching on product names.

Review Repository
Open the ReviewRepository.java file
 Open ReviewRepository.java in IDE

Create a repository for the Review model by extending MongoRepository. This will allow for basic CRUD operations and custom queries.

Add the following method:

findByStoreIdAndProductId: Retrieve reviews for a specific product and store.
Return type: List <Review>

Parameters: Long storeId, Long productId

Use the findBy convention with StoreId and ProductId to filter reviews.

Hint:

Extend MongoRepository<Review, String> to work with MongoDB for review-related CRUD operations.

Use findBy naming convention to create queries that retrieve data based on specific field values (in this case, storeId and productId).

Store Repository
Open the StoreRepository.java file
 Open StoreRepository.java in IDE

Create a repository for the Store model by extending JpaRepository. This will allow for basic CRUD operations and custom queries.

Add the following methods:

findByid: Retrieve a store by its ID.

Return Type: Store

Parameter: Long id

Use findByid to fetch a store based on its ID.

findBySubName: Retrieve stores whose name contains a given substring.

Return Type: List<Store>

Parameter: String pname

Use @Query with LOWER and CONCAT to create a case-insensitive search based on a substring of the store name.

Hint

Extend JpaRepository<Store, Long> to interact with the Store table.

For custom queries, use @Query annotation with JPQL (Java Persistence Query Language).

Use LOWER to make the search case-insensitive, and CONCAT for substring matching.

Order Service
Open the OrderService.java file
 Open OrderService.java in IDE

Autowire the necessary repositories and keep the access specifier as private
For Example:

1
2
@Autowired
 private ProductRepository productRepository;

Copied!

Wrap Toggled!
ProductRepository for accessing the product data.

InventoryRepository for accessing inventory data.

CustomerRepository for validating product IDs and inventory.

StoreRepository for accessing the store data.

OrderDetailsRepository for saving the order details data.

OrderItemRepository for saving the item ordered details data.

Create a service class to manage the order process, including retrieving customer data, creating orders, and saving order items.

Hint: Add @Service annotation above the class definition

Add the following logic:

saveOrder Method: This method processes a customer's order, including saving the order details and associated items.

Parameters: PlaceOrderRequestDTO placeOrderRequest (Request data for placing an order)

Return Type: void (This method doesn't return anything, it just processes the order)

Retrieve or Create the Customer: Check if the customer already exists by their email using findByEmail. If the customer exists, use the existing customer, otherwise, create a new Customer and save it to the repository.

Hint: Use customerRepository.findByEmail() to check for an existing customer and customerRepository.save() to save a new customer.

Retrieve the Store: Fetch the store by ID from storeRepository. If the store doesn't exist, throw an exception.

Hint: Use storeRepository.findById() to retrieve the store.

Create OrderDetails: Create a new OrderDetails object and set customer, store, total price, and the current datetime.

Hint: Set the order date with java.time.LocalDateTime.now() and save the order with orderDetailsRepository.save().

Create and Save OrderItems: For each product purchased, find the corresponding Inventory, update its stock level, and save the changes.

Hint: Use inventoryRepository.findByProductIdandStoreId() to get the inventory and inventoryRepository.save() to update it.

Create OrderItem for each product and associate it with the OrderDetails.

Hint: Use orderItemRepository.save() to save each order item.

Additional Hints:

Managing Customer Creation: If the customer is not found in the database, a new Customer object will be created and saved to the repository. Make sure to handle the case where the customer already exists and reuse the existing Customer.

Store Retrieval: Always check if the store is present in the database by checking the Optional value returned by findById. If not, throw a runtime exception to handle the error.

Order Details: Ensure that the totalPrice is passed correctly from the request data. It’s also important to store the current timestamp for when the order is created. Use java.time.LocalDateTime.now() to capture the time.

Order Item Creation: For each product in the purchaseProduct list, create an OrderItem, and ensure the inventory is updated accordingly. Decrease the stock level of the product in the store's inventory after each purchase.

Stock Level Management: Be mindful of inventory changes. If the product's stock level is updated (decreased in this case), remember to save the modified Inventory object back to the repository.

Handling Multiple Items: If the order contains multiple items, iterate through each product, check the inventory, adjust the stock level, and store each corresponding OrderItem.

ServiceClass
Open the ServiceClass.java file
 Open ServiceClass.java in IDE

Create a service class to validate inventory and products, and fetch related data.

Hint: Add @Service annotation above the class definition

Declare necessary repository to be used as private
For Example:

1
private final InventoryRepository inventoryRepository;

Copied!

Wrap Toggled!
ProductRepository for accessing the product data.

InventoryRepository for accessing inventory data.

Add the following logic:

validateInventory: This method checks whether an inventory record exists for a given product and store combination.

Parameters: Inventory inventory (The inventory object you want to validate)

Return Type: boolean (Returns false if an inventory record already exists for the given product and store, otherwise true)

Hint: Use inventoryRepository.findByProductIdandStoreId() to check for existing inventory records. Return false if the inventory already exists, and true otherwise.

validateProduct: This method checks whether a product exists by its name.

Parameters: Product product (The product you want to validate)

Return Type: boolean (Returns false if a product with the same name already exists, otherwise true)

Hint: Use productRepository.findByName() to check if a product with the same name already exists in the database. If it exists, return false; otherwise, return true.

ValidateProductId: This method validates whether a product exists by its ID.

Parameters: long id (The product ID you want to validate)

Return Type: boolean (Returns false if the product does not exist with the given ID, otherwise true)

Hint: Use productRepository.findByid() to check if the product exists. If the product is null, return false.

getInventoryId: This method fetches the inventory record for a given product and store combination.

Parameters: Inventory inventory (The inventory object to search for)

Return Type: Inventory (Returns the found inventory record for the given product and store combination)

Hint: Use inventoryRepository.findByProductIdandStoreId() to get the inventory record.

Additional Hints:
validateInventory Method:

Use this method to ensure there's no duplicate inventory entry for a product-store pair. If a product is already associated with the store, it will return false, meaning no new inventory can be added.
validateProduct Method:

This check ensures that no two products with the same name exist in your product database. You can expand this validation to check for other attributes if needed (e.g., SKU).
ValidateProductId Method:

This method helps ensure the product exists by its ID. Before attempting to create an order or any other action, you can use this method to validate the existence of a product with a given ID.
getInventoryId Method:

This method is useful when you need to retrieve an existing inventory record for a product in a specific store. It can be used when updating stock levels or performing any inventory-related checks.

