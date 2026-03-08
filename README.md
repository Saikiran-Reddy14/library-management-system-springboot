# Library Management System

A robust RESTful API built with Spring Boot for managing library operations including book inventory, user authentication, and borrowing workflows.

## 🚀 Features

### Authentication & Authorization
- JWT-based authentication with access and refresh tokens
- Role-based access control (USER and ADMIN roles)
- Token blacklisting for secure logout
- Refresh token rotation for enhanced security
- Automatic token cleanup for expired tokens

### Book Management
- CRUD operations for books (Admin only)
- Advanced search across title, author, ISBN, and category
- Filter books by category, author, or availability
- Pagination and sorting support
- Pessimistic locking to prevent race conditions during borrowing

### Borrowing System
- Borrow and return books with due date tracking
- Automatic overdue detection with scheduled tasks
- Borrow history and active borrows tracking
- Filter borrows by status (BORROWED, RETURNED, OVERDUE)
- Prevents duplicate active borrows for the same book

### Additional Features
- Global exception handling with meaningful error messages
- Transaction management for data consistency
- Input validation with custom error responses
- Scheduled cleanup tasks for tokens and overdue books

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot 3.x
- **Security:** Spring Security, JWT (jjwt)
- **Database:** MySQL/PostgreSQL with Spring Data JPA
- **Build Tool:** Maven
- **ORM:** Hibernate

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0+ or PostgreSQL 12+
- Maven 3.6+

## ⚙️ Installation & Setup

1. **Clone the repository**
```bash
git clone <repository-url>
cd library-management
```

2. **Configure database**

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=your_username
spring.datasource.password=your_password

jwt.secret=your-secret-key-minimum-256-bits
jwt.accessTokenExpiration=3600000
jwt.refreshTokenExpiration=86400000
```

3. **Create database**
```sql
CREATE DATABASE library_db;
```

4. **Build and run**
```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### Register User
```http
POST /auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Refresh Token
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "your-refresh-token"
}
```

#### Logout
```http
POST /auth/logout
Authorization: Bearer <access-token>
```

---

### Book Endpoints

#### Get All Books (Paginated)
```http
GET /books?pageNumber=0&pageSize=10&sortBy=bookId&sortOrder=asc
Authorization: Bearer <access-token>
```

#### Get Book by ID
```http
GET /books/{bookId}
Authorization: Bearer <access-token>
```

#### Search Books
```http
GET /books/search?query=java&pageNumber=0&pageSize=10&sortBy=title&sortOrder=asc
Authorization: Bearer <access-token>
```

#### Filter by Category
```http
GET /books/category/fiction?pageNumber=0&pageSize=10
Authorization: Bearer <access-token>
```

#### Filter by Author
```http
GET /books/author/rowling?pageNumber=0&pageSize=10
Authorization: Bearer <access-token>
```

#### Get Available Books
```http
GET /books/available?pageNumber=0&pageSize=10
Authorization: Bearer <access-token>
```

#### Add Book (Admin Only)
```http
POST /books/add
Authorization: Bearer <admin-access-token>
Content-Type: application/json

{
  "title": "Clean Code",
  "authorName": "Robert C. Martin",
  "isbn": "978-0132350884",
  "categoryName": "Programming",
  "totalCopies": 5
}
```

#### Update Book (Admin Only)
```http
PATCH /books/{bookId}/update
Authorization: Bearer <admin-access-token>
Content-Type: application/json

{
  "title": "Updated Title",
  "totalCopies": 10
}
```

#### Delete Book (Admin Only)
```http
DELETE /books/{bookId}
Authorization: Bearer <admin-access-token>
```

---

### Borrow Endpoints

#### Borrow Book
```http
POST /borrow
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "bookId": 1
}
```

#### Return Book
```http
POST /borrow/{recordId}/return
Authorization: Bearer <access-token>
```

#### Get Borrow History
```http
GET /borrow/history
Authorization: Bearer <access-token>
```

#### Get Active Borrows
```http
GET /borrow/active
Authorization: Bearer <access-token>
```

#### Get Borrows by Status
```http
GET /borrow/status/{status}
Authorization: Bearer <access-token>
```
Status values: `borrowed`, `returned`, `overdue`

#### Get Borrows by Status - Admin
```http
GET /borrow/admin/status/{status}?pageNumber=0&pageSize=10&sortBy=recordId&sortOrder=desc
Authorization: Bearer <admin-access-token>
```

---

## 📊 Database Schema

### Users Table
- `user_id` (PK)
- `username`
- `email` (unique)
- `password` (encrypted)
- `role` (USER/ADMIN)

### Books Table
- `book_id` (PK)
- `title`
- `author_name`
- `isbn` (unique)
- `category_name`
- `total_copies`
- `available_copies`

### Borrow Records Table
- `record_id` (PK)
- `user_id` (FK)
- `book_id` (FK)
- `borrow_date`
- `due_date`
- `return_date`
- `borrow_status` (BORROWED/RETURNED/OVERDUE)

### Refresh Tokens Table
- `token_id` (PK)
- `user_id` (FK)
- `token`
- `access_token`
- `expires_at`

### Blacklisted Tokens Table
- `id` (PK)
- `token`
- `expires_at`

---

## 🔒 Security Features

- **Password Encryption:** BCrypt hashing algorithm
- **JWT Tokens:** Stateless authentication with configurable expiration
- **Token Blacklisting:** Prevents reuse of logged-out tokens
- **Refresh Token Rotation:** New tokens issued on refresh, old ones invalidated
- **Role-Based Access:** Separate permissions for users and admins
- **Pessimistic Locking:** Prevents concurrent booking conflicts

---

## 🎯 Business Rules

1. Users cannot borrow the same book twice simultaneously
2. Books can only be deleted if all copies are available (none borrowed)
3. Overdue status is automatically updated daily at midnight
4. Borrow period is 14 days from the borrow date
5. Only the user who borrowed a book can return it
6. Admin can view all borrow records with pagination

---

## 📝 Sample Response Format

### Success Response
```json
{
  "message": "Book Borrowed Successfully",
  "status": 201,
  "data": {
    "recordId": 1,
    "bookTitle": "Clean Code",
    "username": "john_doe",
    "borrowDate": "2024-03-08",
    "dueDate": "2024-03-22",
    "returnDate": null,
    "borrowStatus": "BORROWED"
  },
  "timestamp": "2024-03-08T15:30:45"
}
```

### Error Response
```json
{
  "message": "Book not found with id: 999",
  "status": 404,
  "data": null,
  "timestamp": "2024-03-08T15:30:45"
}
```

---

## 🧪 Testing with Postman

1. Import the API endpoints into Postman
2. Register a new user via `/auth/register`
3. Login to get access and refresh tokens
4. Add the access token to Authorization header: `Bearer <token>`
5. Test book and borrow endpoints

**Note:** To test admin endpoints, manually update the user's role to `ADMIN` in the database.

---

## 🔄 Scheduled Tasks

- **Overdue Detection:** Runs daily at midnight (00:00:00)
- **Token Cleanup:** Runs every hour to remove expired blacklisted tokens

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👤 Author

**Your Name**
- GitHub: https://github.com/Saikiran-Reddy14
- LinkedIn: https://www.linkedin.com/in/saikiran-reddy-kadari/

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

---

## ⭐ Show your support

Give a ⭐️ if this project helped you!
