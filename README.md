# 🛒 Retail Product API (Spring Boot)

A backend REST API project built using **Spring Boot** to simulate a retail application similar to an e-commerce platform.

The project follows a layered architecture with **Controller, Service, Repository, DTO, Mapper, and Exception handling layers**.

🚧 This project is currently under active development.

---

## 🚀 Current Features

### Product Management API

Implemented REST APIs for managing products:

* ✅ Create a new product
* ✅ Get all products
* ✅ Get product by ID
* ✅ Search products by name
* ✅ Get products by category
* ✅ Update product completely (PUT)
* ✅ Partially update product (PATCH)
* ✅ Delete product

### User Management API

Implemented REST APIs for managing users:

* ✅ Create a new user
* ✅ Get all users
* ✅ Get user by ID
* ✅ Search users using dynamic filters
* ✅ Update user completely (PUT)
* ✅ Partially update user (PATCH)
* ✅ Delete user

### Backend Features

* ✅ Spring Data JPA integration
* ✅ MySQL database integration
* ✅ DTO pattern for request and response handling
* ✅ Input validation using Jakarta Validation
* ✅ Global exception handling
* ✅ MapStruct for entity-DTO mapping
* ✅ JPA Specifications for dynamic user search
* ✅ Password encryption using PasswordEncoder
* ✅ Unit testing with JUnit and Mockito
* ✅ Swagger/OpenAPI API documentation

---

## 📚 API Documentation

Swagger UI is available when running the application locally:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

---

## ⚙️ Tech Stack

* Java 17+
* Spring Boot
* Spring Data JPA
* Hibernate
* MySQL
* Maven
* Lombok
* MapStruct
* Jakarta Validation
* Swagger / OpenAPI
* JUnit 5
* Mockito

---

## 🏗️ Project Architecture

The application follows a layered architecture:

```text
Controller
    |
    ↓
Service
    |
    ↓
Repository
    |
    ↓
Database
```

Additional layers:

```text
DTO
 ↓
Request / Response objects

Mapper
 ↓
Entity ↔ DTO conversion

Exception
 ↓
Centralized exception handling
```

---

## 📌 API Endpoints

### Products

| Method | Endpoint                                 | Description              |
| ------ | ---------------------------------------- | ------------------------ |
| GET    | `/api/products`                          | Get all products         |
| GET    | `/api/products/{id}`                     | Get product by ID        |
| GET    | `/api/products/category?name={category}` | Get products by category |
| POST   | `/api/products`                          | Create product           |
| PUT    | `/api/products/{id}`                     | Update product           |
| PATCH  | `/api/products/{id}`                     | Partially update product |
| DELETE | `/api/products/{id}`                     | Delete product           |

### Users

| Method | Endpoint            | Description                        |
| ------ | ------------------- | ---------------------------------- |
| GET    | `/api/users`        | Get all users                      |
| GET    | `/api/users/{id}`   | Get user by ID                     |
| GET    | `/api/users/search` | Search users using dynamic filters |
| POST   | `/api/users`        | Create user                        |
| PUT    | `/api/users/{id}`   | Update user                        |
| PATCH  | `/api/users/{id}`   | Partially update user              |
| DELETE | `/api/users/{id}`   | Delete user                        |

### User Search

The User API supports dynamic filtering using **JPA Specifications**.

Example:

```text
GET /api/users/search?firstName=emily&role=customer
```

Supported search criteria include:

* First name
* Email
* Phone number
* Role

---

## 🧪 Testing

Unit tests are implemented using:

* **JUnit 5**
* **Mockito**

The project includes unit tests for service-layer business logic, including:

* Successful operations
* Resource not found scenarios
* Duplicate resource validation
* Validation and update scenarios
* Partial update scenarios
* Delete operations

---

## ▶️ How to Run

### Prerequisites

* Java 17+
* Maven
* MySQL

### Steps

Clone the repository:

```bash
git clone <repository-url>
```

Navigate to the project:

```bash
cd retailapp
```

Run the application:

```bash
mvn spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```

---

## 🛠️ Work in Progress

Planned features:

* Shopping cart functionality
* Order management
* Payment integration
* Spring Security with JWT authentication
* Pagination and sorting
* Advanced filtering
* Docker deployment
* CI/CD pipeline
* Cloud deployment

---

## 👩‍💻 Author

Preethi Sri B.T
