Part 3: Create Repositories and Services
You are going to create repositories and services in this layer. Repositories handle direct communication with the database, providing methods to fetch, save, update, and delete data, often using JPA. Services sit above repositories and contain the business logic, coordinating between repositories and controllers to process data and enforce rules before returning results.

You will also notice the use of Data Transfer Objects or DTOs. These are classes that are used to transfer data between different layers of the application, such as between the service and controller layers. In the repository layer, DTOs are often used in custom queries to fetch specific fields efficiently, improving performance and security. This approach reduces memory usage, keeps APIs clean, and separates internal entity design from external data structures.

Customer Repository
Open the CustomerRepository.java file
 Open CustomerRepository.java in IDE

Create a repository for the Customer model by extending JpaRepository. This will allow for basic CRUD operations without needing to implement the methods manually.

Add the following methods:

findByEmail: Find a customer by their email address.

Return type: Customer

Parameter: String email

findById: Find a customer by their ID.

Return type: Customer

Parameter: Long id

Hint:

Extend JpaRepository<Customer, Long> to inherit basic CRUD functionality.
Declare custom query methods like findByEmail and findById for additional queries.
For Example:
Customer findByEmail(String email);