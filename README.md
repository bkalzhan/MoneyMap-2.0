# MoneyMap-2.0  
Personal finance management mobile application  

MoneyMap-2.0 is a financial management application currently in the **development stage**, designed to help users track and manage their finances efficiently. It follows a **microservices architecture**, with independent services handling different aspects of financial management.  

The **User Service** is responsible for user-related operations, including authentication, authorization, and user profile management. It ensures secure access through **Spring Security** and manages user data using **Spring Data JPA** with a **PostgreSQL** database. The service is built with **Spring Boot 3** and follows best practices for maintainability, including comprehensive **JUnit** test coverage.  

In addition to the User Service, development has started on the **Earning Service**, which will manage income tracking, categorize earnings, and provide financial insights. This service will integrate with other components of MoneyMap to offer users a **comprehensive** and **data-driven** financial management experience.  

To set up the project, ensure you have **JDK 17**, **Maven**, and **PostgreSQL** installed. Clone the repository, build the project using `mvn clean install`, and run the application with `mvn spring-boot:run`. Unit tests can be executed using `mvn test`.  

Planned future enhancements include completing the **Earning Service**, implementing **JWT-based authentication**, adding **Swagger API documentation**, and introducing **caching mechanisms** for improved performance.  
