### ABOUT HUDDLE

Huddle is a platform for individuals to share thoughts and opinions, publish articles, 
read helpful topics.

### FEATURES
* Come on board to share your ideas, 
* interact with your favorite posts or articles, 
* maybe even gossip or "yap" about your favorite topics.

## Prerequisites

### Backend
- Java 17 or higher
- Maven 3.6+
- PostgreSQL (or your preferred database)

### Frontend
- Node.js 16+ 
- npm or yarn package manager

## Technologies Used

### Backend
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Tokens)
- Swagger/OpenAPI for API documentation
- Maven for dependency management

### Frontend
- React 18
- Material-UI (MUI) 5
- React Router DOM
- Axios for API calls
- React Hooks for state management

## Setup Instructions

### Backend Setup

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
The backend application will start on `http://localhost:8080` or as specified in application.properties.

### Frontend Setup

### 1. Navigate to Frontend Directory
```bash
cd frontend
```

### 2. Install Dependencies
```bash
npm install
# or
yarn install
```

### 3. Configure Environment (Optional)
Create a `.env` file in the frontend directory:
```env
REACT_APP_API_URL=http://localhost:8080
```

### 4. Start the Development Server
```bash
npm start
# or
yarn start
```
The frontend application will start on `http://localhost:3000`.

## Frontend Features

### User Interface
- **Modern Design**: Clean, responsive Material-UI components
- **Dark/Light Theme**: Consistent theming throughout the application
- **Mobile Responsive**: Optimized for all device sizes
- **Smooth Animations**: Fade-in effects and hover transitions

### Authentication
- **Login/Signup**: Secure authentication with JWT tokens
- **Protected Routes**: Route-based access control
- **Auto-logout**: Automatic logout on token expiration

### Dashboard & Home
- **Welcome Screen**: Beautiful gradient hero section
- **Feature Highlights**: Interactive cards showcasing platform capabilities
- **Quick Actions**: Easy navigation to key features
- **Statistics Display**: User engagement metrics

### Articles Management
- **Article Hub**: Comprehensive article browsing with search and filters
- **Grid/List Views**: Toggle between card and list layouts
- **Article Creation**: Rich text editor for content creation
- **Rating System**: 5-star rating system for articles
- **Tagging**: Article categorization with tag support
- **Sort Options**: Sort by newest, oldest, or most popular

### Article Interactions
- **Detailed View**: Full article reading experience
- **Comments System**: Nested comments with voting
- **Comment Voting**: Upvote/downvote functionality
- **Share Functionality**: Social sharing capabilities
- **Report System**: Content moderation through user reports

### User Profiles
- **Profile Pages**: Comprehensive user profile display
- **Avatar Support**: Profile picture functionality
- **Follow System**: User following/followers management
- **Activity Tabs**: Articles, followers, and following sections
- **Profile Editing**: Update bio, location, website, and other details

### Reports & Moderation
- **Report Articles**: Flag inappropriate content
- **Report Categories**: Multiple report reason options
- **Admin Dashboard**: Administrative interface for report management
- **Status Tracking**: Report status updates (Pending, Resolved, Dismissed)

### Navigation & Layout
- **Sidebar Navigation**: Collapsible side navigation
- **Header Bar**: User info and quick actions
- **Breadcrumbs**: Clear navigation paths
- **Search Integration**: Global search functionality

### Responsive Design
- **Mobile First**: Optimized for mobile devices
- **Tablet Support**: Perfect tablet experience
- **Desktop Enhanced**: Rich desktop features
- **Touch Friendly**: Touch-optimized interactions

### Performance Features
- **Lazy Loading**: Optimized content loading
- **Error Boundaries**: Graceful error handling
- **Loading States**: Skeleton loaders and spinners
- **Caching**: Efficient data caching strategies

## Application Structure

### Frontend Directory Structure
```
frontend/
├── public/
│   ├── index.html
│   └── ...
├── src/
│   ├── components/
│   │   ├── Layout.js          # Main layout wrapper
│   │   └── SearchBar.js       # Global search component
│   ├── pages/
│   │   ├── Dashboard.js       # Home/landing page
│   │   ├── Articles.js        # Articles hub with grid/list views
│   │   ├── ArticleDetail.js   # Individual article view
│   │   ├── Profile.js         # User profile pages
│   │   ├── Login.js          # Authentication login
│   │   ├── Signup.js         # User registration
│   │   ├── Reports.js        # Admin reports management
│   │   └── ...
│   ├── services/
│   │   └── api.js            # API service layer
│   ├── utils/
│   │   └── axiosConfig.js    # HTTP client configuration
│   ├── App.js                # Main application component
│   └── index.js              # Application entry point
└── package.json
```

### API Documentation
#### Swagger UI
Once the application is running, you can access the API documentation at:

- Swagger UI: http://localhost:6060/swagger-ui.html
- OpenAPI JSON: http://localhost:6060/v3/api-docs

### API Endpoints

#### Authentication
- POST /huddle/signup - Register a new user
- POST /huddle/login - User login
- POST /huddle/logout - User logout

#### Articles
- GET /huddle/articles/allArticles - Get all articles
  - Query parameters: page, size, sort
- POST /huddle/articles/create - Create a new article
- GET /huddle/articles/article/{id} - Get article by ID
- PUT /huddle/articles/update/{id} - Update an article
- DELETE /huddle/articles/delete/{id} - Delete an article

##### Search and Filter for Articles
- GET /huddle/articles/search - Search and filter articles
    - Query parameters:
        - q (string): Search query for title/content
        - author (string): Filter by author username
        - tag (string): Filter by tag
        - dateFrom (ISO-8601): Filter articles created after this date
        - dateTo (ISO-8601): Filter articles created before this date
        - page (int, default: 0): Page number
        - size (int, default: 20): Items per page
        - sort (string, e.g., createdAt,desc): Sort criteria

#### Comments
- POST /huddle/comments/create - Creates a new comment
- GET /huddle/comments/article/{articleId} - Get comments for an article
    - Query parameters: page, size, sort
- PUT /huddle/comments/update/{id} - Updates a comment
- DELETE /huddle/comments/delete/{id} - Deletes a comment

#### Users/Profiles
- GET /huddle/users/{userId}/followers - Get paginated list of user's followers
    - Query parameters:
        - `page`: Page number (default: 0)
        - `size`: Items per page (default: 20)
        - `sort`: Sort criteria (e.g., `follower.username,asc`)
- GET /huddle/users/{userId}/following - Get paginated list of users being followed
- POST /huddle/users/{userId}/follow - Follow a user
- DELETE /huddle/users/{userId}/unfollow - Unfollow a user
- GET /huddle/users/profile/{userId} - Get user profile information
- PUT /huddle/users/profile/update - Update user profile

### Pagination and Sorting
For paginated endpoints (like followers/following), use these query parameters:
- `page`: Page number starting from 0
- `size`: Number of items per page
- `sort`: Field and direction (e.g., `follower.username,asc` or `follower.joinedDate,desc`)

**Important:** When sorting follow relationships, use dot notation to access nested properties:
- `follower.username,asc` - Sort by follower's username
- `follower.email,desc` - Sort by follower's email
- `follower.joinedDate,desc` - Sort by when the follower joined

## Notification Management

### Get User Notifications
- **URL:** `/huddle/notifications`
- **Method:** `GET`
- **Auth Required:** Yes
- **Query Parameters:**
    - `page` (int, optional): Page number (default: 0)
    - `size` (int, optional): Page size (default: 20)
    - `sort` (string, optional): Sort criteria (e.g., "createdAt,desc")
- **Success Response:**
    - **Code:** 200
    - **Content:**
      ```json
      {
        "success": true,
        "message": "Notifications retrieved successfully",
        "data": {
          "content": [
            {
              "id": "uuid",
              "userId": "uuid",
              "actorId": "uuid",
              "actorUsername": "john_doe",
              "actorProfilePic": "url",
              "message": "John Doe started following you",
              "type": "FOLLOW",
              "relatedEntityId": "uuid",
              "isRead": false,
              "createdAt": "2024-01-01T00:00:00Z"
            }
          ],
          "pageable": {...},
          "totalElements": 10
        }
      }
      ```
- **Error Response:**
    - **Code:** 500
    - **Content:**
      ```json
      {
        "success": false,
        "message": "Failed to retrieve notifications: error message",
        "data": null
      }
      ```

### Get Unread Notification Count
- **URL:** `/huddle/notifications/unread-count`
- **Method:** `GET`
- **Auth Required:** Yes
- **Success Response:**
    - **Code:** 200
    - **Content:**
      ```json
      {
        "success": true,
        "message": "Unread count retrieved successfully",
        "data": 5
      }
      ```
- **Error Response:**
    - **Code:** 500
    - **Content:**
      ```json
      {
        "success": false,
        "message": "Failed to retrieve unread count: error message",
        "data": null
      }
      ```

### Mark All Notifications as Read
- **URL:** `/huddle/notifications/mark-all-read`
- **Method:** `POST`
- **Auth Required:** Yes
- **Success Response:**
    - **Code:** 200
    - **Content:**
      ```json
      {
        "success": true,
        "message": "All notifications marked as read",
        "data": null
      }
      ```
- **Error Response:**
    - **Code:** 500
    - **Content:**
      ```json
      {
        "success": false,
        "message": "Failed to mark notifications as read: error message",
        "data": null
      }
      ```

### Notification Types
- `FOLLOW` - When someone follows you
- `NEW_COMMENT` - When someone comments on your article
- `COMMENT_VOTE` - When someone upvotes/downvotes your comment

### Search and Filter Guidelines
#### Text Search
Most search endpoints support a q or searchTerm parameter that searches across multiple fields:
- Articles: Searches in title and content
- Users: Searches in username, email, first name, and last name
- Comments: Searches in comment body
- Notifications: Searches in notification message

#### Date Range Filtering
Use ISO-8601 format for date filters:
- dateFrom=2024-01-01T00:00:00Z
- dateTo=2024-12-31T23:59:59Z

### Security
The application uses JWT (JSON Web Tokens) for authentication. Include the JWT token in the Authorization header as Bearer <token> for protected endpoints.

### Reports Management
#### Create Report
 - URL: /huddle/reports/create
Method: POST
Auth Required: Yes
Request Body:

#### Get All Reports (Admin Only)
- URL: /huddle/reports/all
- Method: GET
- Auth Required: Yes (Admin authority required)
- Query Parameters:
- status (optional): 
- Filter by report status (PENDING, UNDER_REVIEW, RESOLVED, DISMISSED)
- page (int, optional): Page number (default: 0)
- size (int, optional): Page size (default: 20)
- sort (string, optional): Sort criteria (e.g., "createdAt,desc"

#### Update Report Status (Admin Only)
- URL: /huddle/reports/update/{id}
- Method: PUT
- Auth Required: Yes (Admin authority required)
- Path Parameters: id: Report UUID

#### Report Status Values
- PENDING - Report awaiting review
- UNDER_REVIEW - Report being investigated
- RESOLVED - Report has been addressed
- DISMISSED - Report dismissed as invalid

#### Report Reason Values
Common reasons for reporting:
- INAPPROPRIATE_CONTENT
- SPAM
- HARASSMENT
- MISINFORMATION
- COPYRIGHT_VIOLATION
- OTHER

### Analytics (Admin Only)
#### Get All Analytics
- URL: /huddle/analytics/all
- Method: GET
- Auth Required: Yes (Admin authority required)
- Description: Get comprehensive analytics including user, post, and report statistics

#### Get User Statistics
- URL: /huddle/analytics/users
- Method: GET
- Auth Required: Yes (Admin authority required)
- Description: Get user-related statistics

#### Get Post Statistics
- URL: /huddle/analytics/posts
- Method: GET
- Auth Required: Yes (Admin authority required)
- Description: Get post/article-related statistics

#### Get Report Statistics
- URL: /huddle/analytics/reports
- Method: GET
- Auth Required: Yes (Admin authority required)
- Description: Get report-related statistics

---

### Bookmarks

#### Add Bookmark
- **URL:** `/huddle/bookmarks/{articleId}`
- **Method:** `POST`
- **Auth Required:** Yes
- **Description:** Bookmark an article for later reading
- **Success Response:**
  ```json
  {
    "success": true,
    "message": "Article bookmarked successfully",
    "data": {
      "articleId": "uuid",
      "bookmarked": true
    }
  }
  ```

#### Remove Bookmark
- **URL:** `/huddle/bookmarks/{articleId}`
- **Method:** `DELETE`
- **Auth Required:** Yes
- **Description:** Remove a bookmark from an article

#### Check Bookmark Status
- **URL:** `/huddle/bookmarks/{articleId}/status`
- **Method:** `GET`
- **Auth Required:** Yes
- **Description:** Check if an article is bookmarked by the current user

#### Get User Bookmarks
- **URL:** `/huddle/bookmarks`
- **Method:** `GET`
- **Auth Required:** Yes
- **Query Parameters:**
  - `page` (int, optional): Page number (default: 0)
  - `size` (int, optional): Page size (default: 10)
- **Description:** Get all bookmarked articles for the current user

#### Get Bookmark Count
- **URL:** `/huddle/bookmarks/count`
- **Method:** `GET`
- **Auth Required:** Yes
- **Description:** Get total number of bookmarks for the current user

---

### Password Reset

#### Request Password Reset
- **URL:** `/huddle/password/forgot`
- **Method:** `POST`
- **Auth Required:** No
- **Request Body:**
  ```json
  {
    "email": "user@example.com"
  }
  ```
- **Description:** Initiate password reset process by providing email

#### Validate Reset Token
- **URL:** `/huddle/password/validate-token`
- **Method:** `GET`
- **Auth Required:** No
- **Query Parameters:**
  - `token`: Password reset token
- **Description:** Check if a password reset token is valid

#### Reset Password
- **URL:** `/huddle/password/reset`
- **Method:** `POST`
- **Auth Required:** No
- **Request Body:**
  ```json
  {
    "token": "reset-token-uuid",
    "newPassword": "newSecurePassword123",
    "confirmPassword": "newSecurePassword123"
  }
  ```
- **Description:** Reset password using the token received via email

---

### Article Sharing

#### Get Share Links
- **URL:** `/huddle/articles/{articleId}/share-links`
- **Method:** `GET`
- **Auth Required:** No
- **Description:** Generate share links for various social media platforms
- **Success Response:**
  ```json
  {
    "success": true,
    "message": "Share links generated successfully",
    "data": {
      "twitter": "https://twitter.com/intent/tweet?...",
      "facebook": "https://www.facebook.com/sharer/...",
      "linkedin": "https://www.linkedin.com/sharing/...",
      "email": "mailto:?subject=...",
      "directLink": "https://yourapp.com/articles/uuid"
    }
  }
  ```

#### Record Share
- **URL:** `/huddle/articles/{articleId}/share`
- **Method:** `POST`
- **Auth Required:** Yes
- **Query Parameters:**
  - `platform`: Share platform (TWITTER, FACEBOOK, LINKEDIN, EMAIL, COPY_LINK, OTHER)
- **Description:** Record when a user shares an article

#### Get Share Count
- **URL:** `/huddle/articles/{articleId}/share-count`
- **Method:** `GET`
- **Auth Required:** No
- **Description:** Get total share count for an article
- **Success Response:**
  ```json
  {
    "success": true,
    "message": "Share count retrieved successfully",
    "data": {
      "totalShares": 25,
      "byPlatform": {
        "TWITTER": 10,
        "FACEBOOK": 8,
        "LINKEDIN": 5,
        "COPY_LINK": 2
      }
    }
  }
  ```

---

### Article Drafts

Articles now support a `status` field with the following values:
- `DRAFT` - Article saved but not published
- `PUBLISHED` - Article visible to all users
- `ARCHIVED` - Article hidden from public view

When creating or updating articles, you can set the status to save as draft or publish immediately.

---
The following endpoints require admin privileges (users with ADMIN authority):

#### Reports Management:
- GET /huddle/reports/all - View all reports
- PUT /huddle/reports/update/{id} - Update report status

#### Analytics Dashboard:
- GET /huddle/analytics/all - Comprehensive analytics
- GET /huddle/analytics/users - User statistics
- GET /huddle/analytics/posts - Post statistics
- GET /huddle/analytics/reports - Report statistics

Note: Admin users are identified by having the ADMIN authority in their security context.

---

## Security Features

### Authentication & Authorization
- **JWT Tokens**: Secure JSON Web Token authentication
- **Role-based Access Control**: User and Admin roles with different permissions
- **Password Encryption**: BCrypt password hashing
- **Token Expiration**: Configurable JWT expiration (default: 24 hours)

### Security Headers
All responses include security headers:
- `X-Frame-Options: DENY` - Prevents clickjacking attacks
- `X-XSS-Protection: 1; mode=block` - XSS protection
- `X-Content-Type-Options: nosniff` - Prevents MIME sniffing
- `Referrer-Policy: strict-origin-when-cross-origin`
- `Content-Security-Policy` - Controls resource loading
- `Strict-Transport-Security` - Forces HTTPS connections

### Rate Limiting
API requests are rate-limited to prevent abuse:
- **60 requests per minute** per IP address
- **1000 requests per hour** per IP address
- Rate limit headers included in responses:
  - `X-Rate-Limit-Remaining`: Remaining requests

### Input Sanitization
- HTML tag stripping
- XSS prevention through character escaping
- SQL injection pattern detection
- File name sanitization

### Error Handling
- Stack traces are **never** exposed to users
- All errors return user-friendly messages
- Errors are logged server-side for debugging

---

## Observability & Monitoring

### Health Checks
Access health information at:
```
GET /actuator/health
```

Response includes:
- Application status
- Database connectivity
- Liveness and readiness probes

### Metrics
Prometheus-compatible metrics available at:
```
GET /actuator/prometheus
```

Custom metrics tracked:
- `huddle.articles.created` - Articles created
- `huddle.articles.viewed` - Article views
- `huddle.users.registered` - User registrations
- `huddle.users.login` - Login attempts (success/failed)
- `huddle.comments.created` - Comments created
- `huddle.reports.created` - Reports filed
- `huddle.api.errors` - API errors
- `huddle.rate.limit.exceeded` - Rate limit violations

### Logging
- **Production**: WARN level for third-party libraries, INFO for application
- **Development**: INFO level with SQL query logging
- Pattern: `%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n`

---

## Pagination

All list endpoints support pagination with these query parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 10 | Items per page (max: 100) |
| `sort` | string | varies | Sort field and direction (e.g., `createdAt,desc`) |

### Response Format
Paginated responses include metadata:
```json
{
  "success": true,
  "message": "Items retrieved successfully",
  "content": [...],
  "pagination": {
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10,
    "first": true,
    "last": false,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

## Deployment

### Render Deployment

The project includes a `render.yaml` for one-click deployment to Render:

1. **Connect GitHub Repository** to Render
2. **Create PostgreSQL Database** on Render
3. **Set Environment Variables**:
   - `DATABASE_URL`: PostgreSQL connection string
   - `JWT_SECRET`: Your secret key (min 64 characters)
   - `CORS_ORIGINS`: Frontend URL
4. **Deploy** using the render.yaml blueprint

### Environment Variables

#### Backend (Required)
| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection URL | - |
| `JWT_SECRET` | Secret key for JWT signing | - |
| `JWT_EXPIRATION` | Token expiration in ms | 86400000 |
| `CORS_ORIGINS` | Allowed origins (comma-separated) | localhost |
| `PORT` | Server port | 8080 |

#### Backend (Optional)
| Variable | Description | Default |
|----------|-------------|---------|
| `RATE_LIMIT_RPM` | Requests per minute | 60 |
| `RATE_LIMIT_RPH` | Requests per hour | 1000 |
| `SWAGGER_ENABLED` | Enable Swagger UI | true |
| `UPLOAD_DIR` | File upload directory | uploads |

#### Frontend
| Variable | Description | Default |
|----------|-------------|---------|
| `REACT_APP_API_URL` | Backend API URL | http://localhost:6061 |
| `REACT_APP_DEFAULT_PAGE_SIZE` | Default pagination size | 10 |

### Docker Deployment (Coming Soon)
Docker support will be added in future releases.

---

## API Documentation

### Swagger UI
Once running, access the interactive API documentation:

- **Swagger UI**: `http://localhost:6061/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:6061/v3/api-docs`

The Swagger UI includes:
- All endpoint documentation
- Request/response schemas
- Authentication support (click Authorize, enter `Bearer {token}`)
- Try-it-out functionality

---

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Support

For support, please open an issue in the GitHub repository or contact the maintainers.

