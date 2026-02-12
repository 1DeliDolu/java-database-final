Inventory Model
Open the Inventory.java file 
Add the following attributes along with getters and setters:
id: private long; represents the product id in the inventory

product: private Product; represents the product in the inventory

store: private Store; represents the store where the inventory is stored

stockLevel: private Integer; represents the current stock level of the product at the store

Hint:

Use @Id and @GeneratedValue(strategy = GenerationType.IDENTITY) for id to auto increment it and set it as primary key
Set up relationships:
Product: An inventory entry is associated with one product. Use the @ManyToOne annotation and link it to the Product entity.

Hint:

Use the @JsonBackReference("inventory-product") to handle the bidirectional relationship correctly and prevent circular references during JSON serialization.
Store: An inventory entry is also associated with one store. Use the @ManyToOne annotation and link it to the Store entity.

Hint:

Use @JsonBackReference("inventory-store") to manage the relationship with the store.
Use the @JoinColumn annotation to specify the foreign key column names:
For the product field, use @JoinColumn(name = "product_id").

For the store field, use @JoinColumn(name = "store_id").

Hint:

Create a constructor that takes in a Product, Store, and Integer stockLevel to initialize the Inventory object.
Add @Entity annotation above class name