Project Setup:
-------------

1. Create Project Directory
2. Create Directory Structure
3. Add All Files 
   a. pom.xml
   b. application.properties

4. Install Lombok Plugins
   a. Go to File -> Settings
   b. Navigate to Plugins
   c. Search for "Lombok"
   d. Click Install and Restart IDE

5. Enable Annotation
   a. Go to File -> Settings
   b. Navigate to Build, Execution,Deployment-> Complier -> Annotation    Processors.
   c. Check the "Enable Annotation Processing "
   d. Click Apply and OK

6. Download Dependencies
   a. Open the Maven Panel.
   b. Click the Reload All Maven Projects icon.
   c. Wait for dependencies to download.



Running the Application:
------------------------

1. Using IDE (IntelliJ IDEA)

-> Open IntelliJ IDEA
-> Click "Open" and select the Library Book Management System folder
-> Wait for Maven to download dependencies 
-> Right-click on LibraryBookManagementSystemApplication.java and Select "Run".


Tools, libraries, and frameworks:
---------------------------------

Framework
-> Spring Boot 3.2.0
-> Spring Data JPA
-> Hibernate

Database
-> H2 Database: In-Memory Database
-> SQL


API & Documentation

-> Spring Web
-> Swagger/OpenAPI

Development Tools

-> Lombok
-> Maven

Java 

-> Java 17

URLs:
-----

Swagger UI: http://localhost:8080/swagger-ui.html
H2 Console: http://localhost:8080/h2-console


Approach & Thought Process:
----------------------------

1. Domain-Driven Design
   -> Book: Represents the library inventory
   -> Borrower: Represents library members
   -> BorrowRecord: Represents the transaction and relationships

2. Implementation
   Controller(HTTP Layer) -> Service( Business Logic ) -> Repository(Data Access Layer) -> Database

3. RESTful API
   example : /books , /books/{id} , /borrowers

4. HTTP Methods
   -> GET
   -> POST
   -> PUT
   -> DELETE

5. Status Codes
   -> 200 OK
   -> 201 CREATED
   -> 404 Not Found
   -> 500 Internal Server Error

6. Transaction Management
   -> @Transactional

7. DTO
   -> Don't expose internal structure

8. Exception Handling
   -> Global Exception Handler
      a. To Handle all exceptions
   -> Custom Exception
      a. BookNotFoundException etc.

9. Enum
   -> Prevent invalid values

10. Database
    -> H2 In-Memory Database
    -> UUID - Unique ID's
    -> Relationships: @ManyToOne and @OneToMany


Challenges:
----------

Challenge 1: Handling Overdue Books

Problem: How to identify overdue books without manually checking?

Solution 1: Query

@Query("SELECT br FROM BorrowRecord br " +
       "WHERE br.active = true AND br.returnDate IS NULL " +
       "AND br.dueDate < CURRENT_DATE")
List<BorrowRecord> findOverdueRecords();

Solution 2: Scheduled Job

@Scheduled(cron = "0 0 10 * * ?") // Running the job daily at 10 AM

public void flagOverdueRecords() {
    List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords();
    System.out.println("Found " + overdueRecords.size() + " overdue records");


Challenge 2: To Find “Top Borrowed Books”

Problem: “Top Borrowed Books” requires grouping and counting across tables.

Solution: Query

@Query("SELECT br.book.id as bookId, br.book.title as title, " +
        "COUNT(br) as borrowCount " +
        "FROM BorrowRecord br " +
        "GROUP BY br.book.id, br.book.title " +
        "ORDER BY COUNT(br) DESC")
List<Object[]> findTopBorrowedBooks();

