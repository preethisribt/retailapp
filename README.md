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

### Backend Features

* ✅ Spring Data JPA integration
* ✅ MySQL database integration
* ✅ DTO pattern for request and response handling
* ✅ Input validation using Jakarta Validation
* ✅ Global exception handling
* ✅ MapStruct for entity-DTO mapping
* ✅ Unit testing with JUnit and Mockito
* ✅ Swagger/OpenAPI API documentation

---

## 📚 API Documentation

Swagger UI is available when running the application locally:

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

```
http://localhost:8080/v3/api-docs
```

---

## ⚙️ Tech Stack

* Java
* Spring Boot
* Spring Data JPA
* Hibernate
* MySQL
* Maven
* Lombok
* MapStruct
* Swagger / OpenAPI
* JUnit 5
* Mockito

---

## 🏗️ Project Architecture

```
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

```
DTO  → Request/Response objects
Mapper → Entity ↔ DTO conversion
Exception → Centralized error handling
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

```
http://localhost:8080
```

---

## 🛠️ Work in Progress

Planned features:

* User management
* Shopping cart functionality
* Order management
* Payment integration
* Spring Security with JWT authentication
* Pagination and advanced filtering
* Docker deployment
* CI/CD pipeline
* Cloud deployment

---

## 👩‍💻 Author

Preethi Sri B.T
