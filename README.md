# Rent-films

## Assumptions

1. We don't have security (no tokens), so we pass `userId` in requests. There is no user table.
2. We don't use currency for prices; it is represented as an integer.
3. Films cannot be returned early — rental time must expire.
4. The application is built with Java 21.
5. Focus is on Film CRUD operations and Api documentation.
6. Input validation is implemented.
7. Unit tests cover validation and Film CRUD operations.
8. Rental logic prevents:
    - renting unavailable films
    - renting the same film twice by the same user
9. A flexible pricing strategy was implemented for future extensions.
10. Generation of DTOs and Controller interfaces was implemented using openapi-generator

---

## How to run application

### Run with Docker

```bash
mvn clean install
docker-compose up -d
```

---

### Run locally

Set active Spring profile to `local`, then run:

```bash
mvn clean install
```

---

### Using Task runner

```bash
winget install Task.Task
task restart-backend
```

---

## Access URLs

- Application: http://localhost:8080 (local)
- Application (Docker): http://localhost:8081
- Swagger UI: http://localhost:8080/swagger-ui/index.html
  or http://localhost:8081/swagger-ui/index.html