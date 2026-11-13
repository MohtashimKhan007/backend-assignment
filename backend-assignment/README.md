# GitHub Repository Searcher

Spring Boot REST API that searches GitHub repositories, persists repository data in PostgreSQL (upsert by GitHub ID), and provides filtering and sorting endpoints.

## Live Deployment & API Docs

- Application / Swagger UI: https://github-repo-searcher-jqa5.onrender.com
- Swagger Direct: https://github-repo-searcher-jqa5.onrender.com/swagger-ui/index.html
- OpenAPI JSON Spec: https://github-repo-searcher-jqa5.onrender.com/api-docs

## Tech Stack

- Java 17 / 21
- Spring Boot 3 (Spring Web, Spring Data JPA, Spring Validation)
- PostgreSQL
- Flyway (Database migrations)
- SpringDoc OpenAPI 3 (Swagger UI)
- Docker & Render (Deployment)
- JUnit 5 & Mockito (Testing)

## Database Schema

Table: `github_repositories`

| Column | Type | Details |
| :--- | :--- | :--- |
| `id` | BIGINT | Primary Key (GitHub Repository ID) |
| `name` | VARCHAR(255) | Repository name |
| `description` | TEXT | Repository description |
| `owner_name` | VARCHAR(255) | Owner username |
| `language` | VARCHAR(100) | Indexed |
| `stars` | INTEGER | Indexed |
| `forks` | INTEGER | Indexed |
| `last_updated` | TIMESTAMP | Indexed |

Indexes are created via Flyway migration on `language`, `stars`, `forks`, and `last_updated` for efficient filtering and sorting.

## API Endpoints

### 1. Search and Save Repositories
Fetches repositories from GitHub REST API based on criteria and updates/inserts records into PostgreSQL.

- **Method:** `POST`
- **Path:** `/api/github/search`
- **Request Body:**
```json
{
  "query": "spring boot",
  "language": "Java",
  "sort": "stars"
}
```
- **Response (200 OK):**
```json
{
  "message": "Repositories fetched and saved successfully",
  "repositories": [
    {
      "id": 123456,
      "name": "spring-boot-example",
      "description": "An example repository for Spring Boot",
      "owner": "user123",
      "language": "Java",
      "stars": 450,
      "forks": 120,
      "lastUpdated": "2024-01-01T12:00:00Z"
    }
  ]
}
```

### 2. Retrieve Stored Repositories
Retrieves stored records from the PostgreSQL database with optional filters.

- **Method:** `GET`
- **Path:** `/api/github/repositories`
- **Query Parameters:**
  - `language` (optional): Filter by programming language
  - `minStars` (optional): Filter by minimum star count
  - `sort` (optional): Sort field (`stars`, `forks`, or `updated`). Defaults to `stars`.

Example:
```http
GET /api/github/repositories?language=Java&minStars=100&sort=stars
```

- **Response (200 OK):**
```json
{
  "repositories": [
    {
      "id": 123456,
      "name": "spring-boot-example",
      "description": "An example repository for Spring Boot",
      "owner": "user123",
      "language": "Java",
      "stars": 450,
      "forks": 120,
      "lastUpdated": "2024-01-01T12:00:00Z"
    }
  ]
}
```

## Error Handling

Standard HTTP status codes are used across all endpoints:
- `400 Bad Request`: Validation failure on empty query or invalid JSON body.
- `404 Not Found`: No repositories found for the search criteria.
- `429 Too Many Requests`: GitHub API rate limit reached.
- `502 Bad Gateway`: GitHub API communication error.

## Local Setup

### 1. Clone repository
```bash
git clone https://github.com/MohtashimKhan007/backend-assignment.git
cd backend-assignment/backend-assignment
```

### 2. Configure Database
Ensure PostgreSQL is running and database exists:
```sql
CREATE DATABASE assignment_db;
```

Update `application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/assignment_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 3. Run application
```bash
./mvnw spring-boot:run
```
Swagger UI will be available at `http://localhost:8080/swagger-ui.html`.

### 4. Run tests
```bash
./mvnw clean test
```

## Postman Collection

A preconfigured Postman collection is available in the root directory: `postman_collection.json`. Import this file into Postman to test all endpoints directly.