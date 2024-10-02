# Reservation_system_backend
English version of readme:
Reservation System Backend
This backend serves to manage a reservation system and contains logic for user management, authentication, CRUD operations for various commodities (rooms, lifts, bars), and reservation management. It allows users to book available time slots for selected commodities and manage their reservations.
Features
1. Authentication and Authorization
•	JWT Authentication: The application uses JWT (JSON Web Token) for user authentication. After logging in, the user receives an access and refresh token.
•	Role-based access control (RBAC): The system supports different user roles (USER, MANAGER, ADMIN), with each role having specific permissions to perform actions.
o	USER: Regular users can book commodities and manage their reservations.
o	MANAGER: Managers have broader management options for reservations and commodities.
o	ADMIN: Administrators have full access to the system and can create, edit, and delete all commodities and users.
2. Commodities
Commodities represent services that can be booked (e.g., rooms, bars, lifts). Each commodity has specific parameters (capacity, price, opening hours) and is managed by an administrator or manager.
•	Rooms (Room.java): Include capacity, price per night, and services associated with the room (e.g., "Accommodation").
•	Lifts (Lift.java): Each lift has a capacity, status (e.g., operational or out of service), and an associated service.
•	Bars (Bar.java): Include parameters such as cuisine type, location, and capacity.
Each commodity has an associated service (Serv.java), which describes the basic attributes of the service (e.g., service name and identifier).
2.1. Commodity Management
Administrators and managers can perform CRUD operations using the REST API:
•	Creating commodities: Endpoints allow the addition of new rooms, lifts, and bars. When a new commodity is created, a corresponding service (ServController.java) is also created to make the commodity available for reservations.
•	Editing commodities: Basic parameters of a commodity, such as capacity, price, and opening hours, can be edited.
•	Deleting commodities: Deleting a commodity from the system also cancels all associated reservations.
3. Reservations
The reservation system manages and checks the availability of time slots for each commodity. Users can book available time slots for the commodities they are interested in.
3.1. Available Time Slots
The system supports calculating available time slots for each commodity based on:
•	Opening hours of the commodity (e.g., a bar is open from 12:00 to 23:00).
•	Existing reservations: When querying for available times, the system takes into account existing reservations to avoid overlapping bookings.
•	Type of commodity: For example, rooms can be booked for a full day (midnight to midnight), while bars and lifts can be booked in hourly slots.
•	Time slots are returned in 30-minute intervals, allowing users to select start and end times based on these intervals.
3.2. Booking Process
1.	The user selects a commodity (e.g., room, lift) and a date.
2.	The backend calculates available time slots for the selected day and returns them to the user.
3.	The user selects a time slot, and the backend verifies that the selected time is still available.
4.	If the slot is free, a new reservation is created and saved in the database.
3.3. Reservation Cancellation
Users can cancel their reservation up to a certain time before it starts. Managers can also cancel a reservation if necessary, even after it has been made.
4. Opening Hours
Each commodity has specific opening hours that define when the commodity can be booked:
•	Dynamic checking: When querying for available reservation times, the system checks the opening hours of the selected commodity (e.g., if a lift is open from 08:00 to 18:00, the user cannot book outside of this time frame).
•	Opening hours are managed in separate entities and can be configured by administrators.
5. Security
•	HTTPS: The application runs on a secure HTTPS connection. An SSL certificate is generated and added to the application.
•	Spring Security: The system is secured using Spring Security. Access to individual endpoints is controlled based on user roles, using JWT tokens.
•	CORS: Communication between the frontend and backend running on different ports is allowed by properly configured CORS rules.
Technology Stack
•	Spring Boot 3.x: A framework for building robust applications in Java.
•	Spring Security: For authentication and authorization based on JWT tokens.
•	PostgreSQL: A relational database for storing data about users, commodities, and reservations.
How to Run the Application
Prerequisites
•	Java 17
•	PostgreSQL
•	Maven
Setup and Run
1.	Clone the repository:
git clone https://github.com/your-username/reservation-system-backend.git
cd reservation-system-backend
2.	Set up the PostgreSQL database and configure the application.properties file:
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
3.	Generate an SSL certificate:
keytool -genkey -alias mycert -keyalg RSA -keystore keystore.jks -keysize 2048
4.	Run the application:
mvn spring-boot:run
The application will be running at https://localhost:8443.


Czech version of readme:
Reservation System Backend
Tento backend slouží k řízení rezervačního systému a obsahuje logiku pro správu uživatelů, autentizaci, CRUD operace pro různé komodity (pokoje, lanovky, bary) a správu rezervací. Umožňuje uživatelům rezervovat dostupné časy pro vybrané komodity a spravovat jejich rezervace.
Funkcionality

1. Autentizace a Autorizace
•	JWT Autentizace: Aplikace používá JWT (JSON Web Token) pro autentizaci uživatelů. Po přihlášení získá uživatel přístupový a obnovovací token.
•	Role-based access control (RBAC): Systém podporuje různé uživatelské role (USER, MANAGER, ADMIN), kde každá role má specifická oprávnění pro provádění akcí.
o	USER: Běžní uživatelé mohou rezervovat komodity a spravovat své rezervace.
o	MANAGER: Manažeři mají přístup k širším možnostem správy rezervací a komodit.
o	ADMIN: Administrátoři mají plný přístup k systému a mohou vytvářet, upravovat a mazat všechny komodity a uživatele.

2. Komodity
Komodity představují služby, které lze rezervovat (např. pokoje, bary, lanovky). Každá komodita má své specifické parametry (kapacitu, cenu, otevírací dobu) a je spravována administrátorem nebo manažerem.
•	Pokoje (Room.java): Obsahují kapacitu, cenu za noc, službu spojenou s pokojem (např. "Ubytování").
•	Lanovky (Lift.java): Každá lanovka má kapacitu, stav (např. funkční nebo mimo provoz) a přidruženou službu.
•	Bary (Bar.java): Mají parametry jako typ kuchyně, umístění a kapacitu.
Každá komodita má přiřazenou službu (Serv.java), která popisuje základní atributy služby (např. název a identifikátor služby).
2.1. Správa komodit
Administrátoři a manažeři mohou pomocí REST API provádět CRUD operace:
•	Vytváření komodit: Endpointy umožňují přidávat nové pokoje, lanovky a bary. Když se vytvoří nová komodita, vytvoří se i příslušná služba (ServController.java), aby byla komodita dostupná pro rezervace.
•	Úprava komodit: Upravují se základní parametry komodity, např. kapacita, cena, otevírací hodiny.
•	Mazání komodit: Vymazání komodity ze systému zároveň ruší všechny přidružené rezervace.
3. Rezervace
Rezervační systém zajišťuje správu a kontrolu dostupnosti časů pro jednotlivé komodity. Uživatelé mohou rezervovat volné časové sloty pro komodity, které je zajímají.

3.1. Volné časové sloty
Systém podporuje výpočet dostupných časových slotů pro jednotlivé komodity na základě:
•	Otevíracích hodin komodit (např. bar je otevřený od 12:00 do 23:00).
•	Existujících rezervací: Při dotazu na dostupné časy jsou zohledněny již existující rezervace, aby nedošlo k překrývání rezervací.
•	Typu komodity: Například pokoje lze rezervovat na celý den (od půlnoci do půlnoci), zatímco bary a lanovky na hodinové sloty.
•	Časy se vracejí v 30minutových intervalech, kdy uživatelé mohou rezervovat začátky a konce rezervací na základě těchto intervalů.
3.2. Proces rezervace
1.	Uživatel vybere komoditu (např. pokoj, lanovku) a datum.
2.	Backend vypočítá dostupné časové sloty pro daný den a vrátí je uživateli.
3.	Uživatel vybere časový úsek a backend ověří, zda je vybraný čas stále dostupný.
4.	Pokud je slot volný, vytvoří se nová rezervace a uloží se do databáze.
3.3. Zrušení rezervace
Uživatelé mohou zrušit svou rezervaci do určitého času před jejím začátkem. Manažeři mohou zrušit rezervaci v případě potřeby i po jejím vytvoření.

4. Otevírací hodiny
Každá komodita má specifické otevírací hodiny, které definují, kdy je možné danou komoditu rezervovat:
•	Dynamická kontrola: Při dotazu na volné časy pro rezervaci systém zohledňuje otevírací dobu dané komodity (např. pokud je lanovka otevřená od 08:00 do 18:00, uživatel nebude moci provést rezervaci mimo tento čas).
•	Otevírací hodiny jsou spravovány v samostatných entitách a jsou konfigurovatelné administrátory.

5. Bezpečnost
•	HTTPS: Aplikace běží na zabezpečeném HTTPS připojení. Certifikát pro SSL je generován a přidán do aplikace.
•	Spring Security: Systém je zabezpečen pomocí Spring Security. Přístup k jednotlivým endpointům je řízen na základě uživatelských rolí, a to pomocí JWT tokenů.
•	CORS: Je povolena komunikace mezi frontendem a backendem, která běží na jiných portech, pomocí správně nakonfigurovaných CORS pravidel.
Technologie
•	Spring Boot 3.x - Framework pro rychlou tvorbu robustních aplikací v Javě.
•	Spring Security - Pro autentizaci a autorizaci na základě JWT tokenů.
•	PostgreSQL - Relační databáze pro ukládání dat o uživatelích, komoditách a rezervacích.

Jak aplikaci spustit
Prerequisites
•	Java 17
•	PostgreSQL
•	Maven

Nastavení a spuštění
1.	Naklonujte repozitář:
git clone https://github.com/your-username/reservation-system-backend.git
cd reservation-system-backend
2.	Nastavte PostgreSQL databázi a upravte soubor application.properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
3.	Vygenerujte SSL certifikát:
keytool -genkey -alias mycert -keyalg RSA -keystore keystore.jks -keysize 2048
4.	Spusťte aplikaci:
mvn spring-boot:run
Aplikace bude běžet na https://localhost:8443.

