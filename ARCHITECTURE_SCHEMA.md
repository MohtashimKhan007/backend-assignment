# GitHub Repository Searcher & Manager — Architecture & Schema

## 🏗 High-Level System Architecture

```mermaid
flowchart TD
    Client["Client / Recruiter / Postman"] -->|HTTP REST Requests| Controller["GithubController\n(@RestController)"]
    Controller -->|Input Validation| DTO["SearchRequest DTO\n(@Valid, @NotBlank)"]
    Controller -->|Delegates to| Service["GithubService\n(@Service)"]
    
    subgraph External_Integration ["GitHub Integration"]
        Service -->|HTTP GET + User-Agent| GitHubAPI["GitHub REST API\n(api.github.com)"]
        GitHubAPI -->|JSON Items| Service
    end

    subgraph Persistence_Layer ["PostgreSQL Storage (Render Managed)"]
        Service -->|Upsert saveAll| Repo["GithubRepo\n(Spring Data JPA)"]
        Repo -->|Dynamic Sort ORDER BY| DB[("PostgreSQL Database\ngithub_repositories")]
    end

    subgraph Error_Handling ["Resilience & Docs"]
        GlobalHandler["GlobalExceptionHandler\n(@RestControllerAdvice)"] -.->|Catch 400, 404, 429, 502| Client
        Swagger["OpenAPI 3 / Swagger UI\n(/swagger-ui.html)"] -.->|Interactive Docs| Client
    end

    classDef primary fill:#2563eb,stroke:#1d4ed8,stroke-width:2px,color:#fff;
    classDef secondary fill:#059669,stroke:#047857,stroke-width:2px,color:#fff;
    classDef db fill:#d97706,stroke:#b45309,stroke-width:2px,color:#fff;
    classDef error fill:#dc2626,stroke:#b91c1c,stroke-width:2px,color:#fff;

    class Controller,Service primary;
    class GitHubAPI,Swagger secondary;
    class DB,Repo db;
    class GlobalHandler error;
```

---

## 🗄 PostgreSQL Database Schema (`github_repositories`)

```mermaid
erDiagram
    GITHUB_REPOSITORIES {
        BIGINT id PK "GitHub Repository Unique ID"
        VARCHAR(255) name "Repository Name"
        TEXT description "Repository Description"
        VARCHAR(255) owner_name "Owner Username"
        VARCHAR(100) language "Programming Language (Indexed)"
        INTEGER stars "Count of Stars (Indexed)"
        INTEGER forks "Count of Forks (Indexed)"
        TIMESTAMP last_updated "ISO-8601 Updated At (Indexed)"
    }
```

### ⚡ Performance Indexes:
- `idx_github_repositories_language` ON `language`
- `idx_github_repositories_stars` ON `stars`
- `idx_github_repositories_forks` ON `forks`
- `idx_github_repositories_last_updated` ON `last_updated`

---

## 🔄 End-to-End Execution Flow

```mermaid
sequenceDiagram
    autonumber
    actor User as User / Postman
    participant Controller as GithubController
    participant Service as GithubService
    participant GitHub as GitHub REST API
    participant DB as PostgreSQL DB

    Note over User,DB: Flow 1: Search & Upsert (POST /api/github/search)
    User->>Controller: POST /api/github/search { query, language, sort }
    Controller->>Service: fetchAndSaveRepositories(request)
    Service->>GitHub: GET /search/repositories?q=... (with User-Agent & Bearer Token)
    GitHub-->>Service: 200 OK (List of 30 items)
    Service->>DB: saveAll(entities) [Upsert by ID]
    DB-->>Service: Updated/Inserted Entities
    Service-->>Controller: List<GithubRepository>
    Controller-->>User: 200 OK ("Repositories fetched and saved successfully")

    Note over User,DB: Flow 2: Retrieve Stored Results (GET /api/github/repositories)
    User->>Controller: GET /api/github/repositories?language=Java&minStars=100&sort=stars
    Controller->>Service: getFilteredRepositories(language, minStars, sort)
    Service->>DB: findFilteredRepositories(language, minStars, Sort.by("stars"))
    DB-->>Service: Filtered & Sorted Rows (via PostgreSQL Indexes)
    Service-->>Controller: List<GithubRepository>
    Controller-->>User: 200 OK (Clean Repository List)
```
