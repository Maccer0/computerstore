# Computer Store Spring Web Application
This is a Java Spring web application for an ecommerce website that includes Spring Security for authentication and authorization. The application is built using Spring MVC framework and utilizes various technologies such as Spring Boot, Thymeleaf, JPA, MySQL, and Spring Security for securing the application and managing user authentication and authorization.

## Installation
1. Clone the repository to your local machine using the following command:
```bash
git clone https://github.com/yourusername/ecommerce-java-spring-web-app.git
```
2. Navigate to the project directory:
```bash
cd ecommerce-java-spring-web-app
```
3. Import the project into your preferred IDE (e.g. IntelliJ IDEA, Eclipse) as a Maven project.

4. Update the MySQL database configurations in src/main/resources/application.properties file, including spring.datasource.url, spring.datasource.username, and spring.datasource.password to match your local MySQL settings.

5. Configure Spring Security by updating the security configurations in src/main/java/com/yourcompany/ecommerce/configuration/SecurityConfig.java file, such as defining user roles, permissions, and authentication providers.

6. Run the application by executing the main class ComputerStoreApplication.java or by using the Maven command:

```bash
mvn spring-boot:run
```
7. Open a web browser and access the application at http://localhost:8080 to start using the ecommerce web application with Spring Security enabled.
## Features
The ecommerce Java Spring web application with Spring Security provides the following features in addition to the ones mentioned in the previous readme:

- Authentication and Authorization
- User registration and login
- Password hashing and validation
- User role-based access control (e.g. admin, customer)
- Secure endpoints for authenticated users only
- Password Reset
- Password reset functionality using email verification
- Generate and send password reset tokens via email
- Reset password with a new password
## Technologies Used
- Java 8
- Spring MVC
- Spring Boot
- Thymeleaf
- JPA (Java Persistence API)
- MySQL
- Spring Security
