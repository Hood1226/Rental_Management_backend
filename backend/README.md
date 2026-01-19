# Rental Management System - Backend

Spring Boot 3.2.0 application with Java 21 for Rental and Sales Management System.

## Technology Stack
- Java 21
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security
- PostgreSQL
- JWT Authentication
- Lombok
- MapStruct
- Swagger/OpenAPI

## Prerequisites
- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+

## Setup Instructions

1. Create PostgreSQL database:
```sql
CREATE DATABASE rental_management_db;
```

2. Run the schema script:
```bash
psql -U postgres -d rental_management_db -f ../database/schema.sql
```

3. Update `application.yml` with your database credentials

4. Build the project:
```bash
mvn clean install
```

5. Run the application:
```bash
mvn spring-boot:run
```

## API Documentation
Once the application is running, access Swagger UI at:
- http://localhost:8080/api/swagger-ui.html

## Project Structure
```
src/main/java/com/rental/
├── entity/          # JPA entities
├── repository/      # JPA repositories
├── service/         # Business logic
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── config/         # Configuration classes
└── exception/      # Exception handlers
```


