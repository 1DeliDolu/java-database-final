Inventory Repository
Open the InventoryRepository.java file
 Open InventoryRepository.java in IDE

Add the following methods:
findByProductIdandStoreId: Find an inventory record by its product ID and store ID.

Return type: Inventory

Parameters: Long productId, Long storeId

Query: "SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.store.id = :storeId"

findByStore_Id: Find a list of inventory records for a specific store.

Return type: List<Inventory>

Parameter: Long storeId

deleteByProductId: Delete all inventory records related to a specific product ID.

Return type: void

Parameter: Long productId

Use @Modifying and @Transactional annotations to modify the database and ensure the transaction is managed correctly.

Hint:

Extend JpaRepository<Inventory, Long> for basic CRUD functionality.

Use @Query for custom queries, @Modifying for update/delete queries, and @Transactional to handle transactions.