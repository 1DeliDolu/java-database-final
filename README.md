Product Model
Open the Product.java file 

Add the following attributes along with getters and setters:
id: private long; auto increment

name: private String; cannot be empty

category: private String; cannot be empty

price: private Double; cannot be empty

sku: private String; cannot be empty, must be unique

Hint:

Use @Id and @GeneratedValue(strategy = GenerationType.IDENTITY) for id to auto increment it and set it as primary key
Use @NotNull for the name, category, price, and sku fields to ensure they are not null when creating a Product object.
Use the @Table annotation with a uniqueConstraints attribute to enforce uniqueness on the sku column. Example: `@Table(name = “product”,
uniqueConstraints = @UniqueConstraint(columnNames = “sku”)) - Add@Entity` annotation above class name

Set up relationships:
Inventory: A product can have multiple inventory entries. Use the @OneToMany annotation to reflect this relationship.

Hint:

Use the mappedBy attribute to indicate the relationship with the Inventory class.

Apply @JsonManagedReference("inventory-product") to handle the bidirectional relationship and prevent circular references during JSON serialization.