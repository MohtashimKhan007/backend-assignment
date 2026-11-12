# GitHub Repository Searcher & Manager

A production-ready Spring Boot backend application that fetches repositories from the GitHub REST API, performs database upserts in PostgreSQL, and provides advanced filtering and sorting endpoints.

## Tech Stack
* Language: Java 21
* Framework: Spring Boot (Spring Web, Spring Data JPA)
* Database: PostgreSQL
* Testing: JUnit 5, Mockito
* Architecture Design: Strict Constructor Injection, DTO Pattern, Global Exception Handling

---

## Project Setup & How to Run

### Prerequisites
* Java 21 installed
* PostgreSQL installed and running
* Maven installed

### Step 1: Database Setup
Open your PostgreSQL shell or pgAdmin and create a database:
```sql
CREATE DATABASE github_db;