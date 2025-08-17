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

## Technologies Used
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Tokens)
- Swagger/OpenAPI for API documentation
- Maven for dependency management

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

### API Documentation
#### Swagger UI
Once the application is running, you can access the API documentation at:

- Swagger UI: http://localhost:6060/swagger-ui.html
- OpenAPI JSON: http://localhost:6060/v3/api-docs

### API Endpoints

#### Authentication
- POST /huddle/signup - Register a new user
- POST /huddle/login - User login

#### Articles
- GET /huddle/articles/allArticles - Get all articles
- POST /huddle/articles/create - Create a new article
- GET /huddle/articles/article/{id} - Get article by ID
- PUT /huddle/articles/update/{id} - Update an article
- DELETE /huddle/articles/delete/{id} - Delete an article

#### Comments
- POST /huddle/comments/create - Creates a new comment
- GET /huddle/comments/article/{articleId} - Get comments for an article
- PUT /huddle/comments/update/{id} - Updates a comment
- DELETE /huddle/comments/delete/{id} - Deletes a comment

### Security
The application uses JWT (JSON Web Tokens) for authentication. Include the JWT token in the Authorization header as Bearer <token> for protected endpoints.





