# reservation_system
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
bash
Zkopírovat kód
git clone https://github.com/your-username/reservation-system-backend.git
cd reservation-system-backend
2.	Nastavte PostgreSQL databázi a upravte soubor application.properties:
properties
Zkopírovat kód
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
3.	Vygenerujte SSL certifikát:
bash
Zkopírovat kód
keytool -genkey -alias mycert -keyalg RSA -keystore keystore.jks -keysize 2048
4.	Spusťte aplikaci:
bash
Zkopírovat kód
mvn spring-boot:run
Aplikace bude běžet na https://localhost:8443.

