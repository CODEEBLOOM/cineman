# Cineman Backend Service

Backend service for the cinema management system, built with Spring Boot.

## Stack

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL
- Flyway
- Swagger / OpenAPI
- WebSocket
- Maven Wrapper

## Main Structure

Source code is mainly under:

- `src/main/java/com/codebloom/cineman`
- `src/main/resources`

Key packages:

- `config`: security, JWT, CORS, Flyway, OpenAPI, WebSocket
- `controller`: client-facing APIs
- `controller/admin`: admin APIs
- `service` and `service/impl`: business logic
- `repository`: JPA data access
- `model`: JPA entities
- `common`: enums, constants, utilities

See `Structure.md` for more details.

## Dev Requirements

You need:

- JDK 21
- PostgreSQL running locally
- Maven is optional because `mvnw.cmd` is included

Current local defaults:

- Server port: `8081`
- Active profile: `dev`
- Database: PostgreSQL on `localhost:5432`

## Current PostgreSQL Configuration

Main config files:

- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-test.yml`
- `src/main/resources/application-prod.yml`

Important notes:

- `application-dev.yml` uses `jdbc:postgresql://localhost:5432/cineman`
- `application-test.yml` uses `jdbc:postgresql://localhost:5432/cineman_test`
- Hibernate is set to `ddl-auto=update`
- Flyway is currently disabled in PostgreSQL profiles because the legacy migration scripts still contain SQL Server syntax

Before running, make sure:

- PostgreSQL is listening on port `5432`
- Databases `cineman` and `cineman_test` exist if you want to keep that layout
- Username/password in the YAML files match your local PostgreSQL setup

## Run For Development

From the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Or build and run the jar:

```powershell
.\mvnw.cmd clean package
java -jar target\cineman.jar
```

After startup:

- Base API: `http://localhost:8081/api/v01`
- Swagger UI: `http://localhost:8081/swagger-ui/index.html`

## Docker

The repo now ships with PostgreSQL in `docker-compose.yml`.

Run:

```powershell
docker compose up --build
```

Services:

- PostgreSQL: `localhost:5432`
- Backend: `localhost:8081`

Notes:

- the backend runs with profiles `dev,docker`
- PostgreSQL data is stored in the `postgres_data` volume
- uploaded files are stored in the `uploads_data` volume and mapped to `/app/uploads`
- Flyway stays disabled in Docker because the legacy SQL scripts are still SQL Server-specific

## Flyway Status

Flyway is still present in the project, but it is disabled in the PostgreSQL profiles.

Reason:

- existing migration files under `src/main/resources/dev/db/migration`
- those scripts still use SQL Server specific syntax such as `GETDATE()`, `sysobjects`, and other SQL Server-only constructs

At the moment, PostgreSQL schema bootstrap relies on Hibernate update mode rather than Flyway.

## RBAC Summary

The project uses RBAC with these tables:

- `users`
- `user_roles`
- `roles`
- `role_permissions`
- `permissions`

Access is checked by matching:

- authenticated user
- assigned permissions from roles
- current HTTP method
- current request URL

See `Structure.md` for the detailed RBAC explanation.

## License

There is still no `LICENSE` file in the repository, and the `pom.xml` license section is empty.
