
Reservation System – Backend
The backend of the Reservation System is built using Spring Boot and communicates with the frontend (see Reservation System Frontend). It manages user authentication, role-based access control, data storage, and the core functionality of the system such as reservations, commodity management, and statistics.

Main Features:
User Authentication and Authorization:
JWT-based authentication to secure endpoints.
Role-based access control with roles: ADMIN, MANAGER, and USER.
Integrated Spring Security for secure and flexible access management.

Commodity Management:
APIs to manage commodities such as rooms, bars, and lifts.
Add, edit, delete, and fetch commodities.
Reservation System:
Manage reservation times for commodities.
Check availability and book commodities.

User Management:
User registration, login, and profile updates.
Role-specific functionalities for ADMIN, MANAGER, and USER.

Statistics and Reports:
Generate statistics such as the number of reservations, most frequently booked commodities, and more.

Database Integration:
PostgreSQL database used for data storage.
Hibernate and JPA are used for ORM and database transactions.
Backend Structure
The project structure is modular and follows standard Spring Boot conventions:

Controllers:
Handle incoming HTTP requests and map them to appropriate services.
Example: ReservationController, AuthController, etc.

Services:
Contain the business logic of the application.
Example: ReservationService, UserService.
Repositories:
Interact with the PostgreSQL database using JPA/Hibernate.
Example: UserRepository, ReservationRepository.

Security:
Custom implementation of Spring Security to manage authentication and authorization.
JWT token handling and role-based access.


How to Run the Backend
Clone the Repository:

git clone https://github.com/your-username/reservation-system-backend.git
cd backend

Set Up the Database:
Ensure PostgreSQL is installed and running.
Create a database (e.g., reservation_system).

Update the database connection details in src/main/resources/application.properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/reservation_system
spring.datasource.username=your-username
spring.datasource.password=your-password
Run the Application:

Use the following command to build and run the application:
mvn spring-boot:run
The backend will be available at https://localhost:8443.

Import HTTPS Certificates:
The backend uses HTTPS for secure communication.
Generate and import SSL certificates if not already done.

Prerequisites
Java 17 or later.
Maven 3.6 or later.
PostgreSQL 13 or later.