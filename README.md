# GitHub Repository Searcher & Manager API

A production-ready Spring Boot backend application that interacts with the GitHub REST API to search repositories, performs efficient database upserts in PostgreSQL, and provides advanced filtering and sorting endpoints.

---

## 🚀 Live Application & API Documentation

- **Live Deployed API Base URL:** `https://github-repo-searcher-jqa5.onrender.com`
- **Interactive Swagger UI:** `https://github-repo-searcher-jqa5.onrender.com/swagger-ui.html`
- **OpenAPI JSON Spec:** `https://github-repo-searcher-jqa5.onrender.com/api-docs`

*(For local execution, open: `http://localhost:8080/swagger-ui.html`)*

---

## 🛠 Tech Stack & Architecture

- **Language:** Java 21 / 17
- **Framework:** Spring Boot (Spring Web MVC, Spring Data JPA, Spring Validation)
- **Database:** PostgreSQL (with Flyway database migrations and indexed columns)
- **API Documentation:** SpringDoc OpenAPI 3 (Swagger UI)
- **Containerization & Deployment:** Docker (Multi-stage build) & Render Blueprint (`render.yaml`)
- **Testing:** JUnit 5, Mockito, MockMvc, H2 in-memory DB for isolated testing

### Architecture & Design Highlights:
1. **Separation of Concerns:** Clean layering into Controller, Service, Repository, DTO, and Entity layers.
2. **Efficient Upsert Operations:** Repositories are stored with GitHub's unique repository `id` as the primary key. Existing repositories are seamlessly updated without duplicating records.
3. **Database-Level Sorting & Indexing:** Sorting (`stars`, `forks`, `updated`) and filtering are executed directly in PostgreSQL with indexed columns for maximum query performance.
4. **Resilient Error & Rate-Limit Handling:** Custom `GlobalExceptionHandler` with dedicated handling for GitHub API rate limits (HTTP 429/403), not found scenarios (HTTP 404), and validation errors (HTTP 400).
5. **Dynamic Cloud Database Adapter:** Automatically parses and adapts Render/Heroku `postgres://` connection strings into standard JDBC format (`jdbc:postgresql://`).

---

## 📖 API Specifications

### 1. Search GitHub Repositories & Upsert to DB
Fetches repositories from GitHub REST API matching criteria and saves/updates them in PostgreSQL.

- **Endpoint:** `POST /api/github/search`
- **Content-Type:** `application/json`

#### Request Body:
```json
{
  "query": "spring boot",
  "language": "Java",
  "sort": "stars"
}
```

#### Response (200 OK):
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

---

### 2. Retrieve Stored Repositories
Retrieves saved repository records from PostgreSQL with optional filtering and sorting.

- **Endpoint:** `GET /api/github/repositories`
- **Query Parameters:**
  - `language` *(optional)*: Filter by programming language (case-insensitive, e.g., `Java`, `Python`, `Go`)
  - `minStars` *(optional)*: Minimum number of stars (e.g., `100`)
  - `sort` *(optional)*: Sort field: `stars`, `forks`, or `updated` (default: `stars`)

#### Example Request:
```http
GET /api/github/repositories?language=Java&minStars=100&sort=stars
```

#### Response (200 OK):
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

---

## ⚡ Error Handling & Edge Cases

| Scenario | HTTP Status | Response Details |
| :--- | :--- | :--- |
| **Empty/Blank Query** | `400 Bad Request` | Returns validation error details for `query` field |
| **No Repositories Found** | `404 Not Found` | Clear message: *"No repositories found on GitHub for the given criteria."* |
| **GitHub Rate Limit Exceeded** | `429 Too Many Requests` | Friendly advice to wait or configure an optional `GITHUB_TOKEN` |
| **External API Failure** | `502 Bad Gateway` | Clear downstream communication error message |

---

## 💻 Running Locally

### Prerequisites
- JDK 17 or JDK 21 installed
- PostgreSQL installed and running

### Step 1: Clone Repository
```bash
git clone https://github.com/MohtashimKhan007/backend-assignment.git
cd backend-assignment/backend-assignment
```

### Step 2: Configure Database
Ensure PostgreSQL database exists:
```sql
CREATE DATABASE assignment_db;
```

Update `application.properties` credentials if different from default:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/assignment_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### Step 3: Run the Application
```bash
./mvnw spring-boot:run
```

Once running:
- Application Base URL: `http://localhost:8080`
- Swagger UI Documentation: `http://localhost:8080/swagger-ui.html`

### Step 4: Run Tests
```bash
./mvnw clean test
```

---

## 🐳 Docker & Render Deployment

### Render Blueprint (Automated 1-Click Deployment)
This repository includes a preconfigured `render.yaml` infrastructure-as-code file:
1. Go to [Render Dashboard](https://dashboard.render.com).
2. Click **New +** -> **Blueprint**.
3. Connect your repository: `https://github.com/MohtashimKhan007/backend-assignment`.
4. Render will automatically provision:
   - A managed PostgreSQL database (`github-searcher-db`)
   - A Dockerized web service (`github-repo-searcher`)
5. Click **Apply** to deploy!
