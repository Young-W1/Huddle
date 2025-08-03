### ABOUT HUDDLE

Huddle is a platform for individuals to share thoughts and opinions, publish articles, 
read helpful topics.

### FEATURES
* Come on board to share your ideas, 
* interact with your favorite posts or articles, 
* maybe even gossip or "yap" about your favorite topics.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL (or your preferred database)

## Setup Instructions

### 1. Clone the Repository
```bash
git clone <https://github.com/Young-W1/Huddle>
cd huddle
```
### 2. Configure Database
Create a PostgreSQL database and update the `application.properties` file with your database credentials.

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```
The application will start on `http://localhost:6060` or else otherwise specified in the application.properties.





