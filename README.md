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

### Admin Access
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