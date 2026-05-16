# Project Structure

## Overview

This project follows a standard Spring Boot layered architecture:

- `controller` receives HTTP requests
- `service` handles business logic
- `repository` talks to the database
- `model` maps Java entities to database tables
- `config` holds infrastructure configuration

Application entry point:

- `src/main/java/com/codebloom/cineman/CinemanApplication.java`

## Main Tree

```text
cineman/
|-- docker-compose.yml
|-- Dockerfile
|-- pom.xml
|-- mvnw
|-- mvnw.cmd
|-- uploads/
|-- src/
|   |-- main/
|   |   |-- java/com/codebloom/cineman/
|   |   |   |-- common/
|   |   |   |-- component/
|   |   |   |-- config/
|   |   |   |-- controller/
|   |   |   |   |-- admin/
|   |   |   |   |-- auth/
|   |   |   |   |-- request/
|   |   |   |   |-- response/
|   |   |   |   `-- websocket/
|   |   |   |-- exception/
|   |   |   |-- listener/
|   |   |   |-- message/
|   |   |   |-- model/
|   |   |   |-- repository/
|   |   |   |-- scheduler/
|   |   |   `-- service/
|   |   `-- resources/
|   |       |-- application.yml
|   |       |-- application-dev.yml
|   |       |-- application-test.yml
|   |       |-- application-prod.yml
|   |       |-- application-secret.yml
|   |       `-- dev/db/migration/
|   `-- test/
```

## Package Responsibilities

### `config`

Infrastructure configuration, including:

- `SecurityConfiguration`
- `JwtFilter`
- `FlywayConfiguration`
- `OpenAPIConfig`
- `WebSocketConfiguration`
- `CORSConfiguration`

### `controller`

Client/public APIs such as:

- movie
- show time
- ticket
- snack
- invoice
- promotion
- user
- membership rank

### `controller/admin`

Administration APIs such as:

- cinema theater
- cinema type
- movie
- genre
- movie role
- participant
- show time
- seat
- user
- promotion
- permission

### `controller/request` and `controller/response`

DTOs for request payloads and API responses.

### `model`

JPA entities such as:

- `UserEntity`
- `RoleEntity`
- `PermissionEntity`
- `MovieEntity`
- `ShowTimeEntity`
- `InvoiceEntity`

### `repository`

Spring Data JPA repositories.

### `service` and `service/impl`

Business logic interfaces and implementations.

### `common`

Shared enums, constants, validators, and helper utilities.

### `resources`

Application configs and legacy Flyway migration files.

## Runtime Configuration

### Profiles

Maven profile defaults to `dev`.

Profile files:

- `application.yml`
- `application-dev.yml`
- `application-test.yml`
- `application-prod.yml`

### Port And API Prefix

- Server port: `8081`
- API base path: `/api/v01`

Examples:

- `/api/v01/auth/login`
- `/api/v01/movie/all`
- `/api/v01/admin/permissions/all`

### Database

The project now targets PostgreSQL.

Current profile defaults:

- dev DB: `cineman`
- test DB: `cineman_test`
- default local port: `5432`

Hibernate is configured with `ddl-auto=none`. Schema is owned by Flyway.

### Flyway

Flyway is enabled and authoritative for schema. Hibernate does not create or alter tables.

How it runs:

- `config/FlywayConfiguration.java` declares a `Flyway` bean and calls `flyway.migrate()` at startup
- The bean is gated by `@ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", havingValue = "true")`
- `application-dev.yml` sets `spring.flyway.enabled: true` and `baseline-on-migrate: true`

Migration locations (`spring.flyway.locations` in `application-dev.yml`):

- `src/main/resources/dev/db/migration` — older scripts `V1..V6`, all PostgreSQL-compatible
- `src/main/resources/db/migration` — active migrations from `V7` onwards

When adding migrations, place them in `db/migration`, bump to the next `V{n}__name.sql`, and use idempotent PostgreSQL syntax (`IF EXISTS` / `IF NOT EXISTS`) to match the existing style.

### Docker Profile

The repository also includes `application-docker.yml`.

It is intended to run together with the `dev` profile and overrides environment-dependent values for:

- datasource URL and credentials
- upload base path inside the Linux container
- OpenAPI server URL

## Request Flow

```text
HTTP Request
   |
   v
Controller
   |
   v
Service
   |
   v
Repository
   |
   v
PostgreSQL
```

Protected endpoints go through:

```text
HTTP Request
   |
   v
JwtFilter
   |
   +-- bypass if endpoint is public
   |
   +-- validate JWT
   |
   +-- load permissions for the current user
   |
   +-- match HTTP method + URL
   |
   v
Controller -> Service -> Repository -> DB
```

## RBAC And Permission Tables

The project uses RBAC plus URL-based permissions.

### Related Tables

```text
users
  |
  | 1-n
  v
user_roles
  |
  | n-1
  v
roles
  |
  | n-n
  v
role_permissions
  |
  | n-1
  v
permissions
```

Meaning:

- `users`: account data
- `user_roles`: roles assigned to users
- `roles`: system roles such as admin, guest, user
- `role_permissions`: join table between roles and permissions
- `permissions`: detailed permissions by HTTP method and URL pattern

### Table Definitions From Entities

#### `permissions`

Defined by `PermissionEntity`.

Main fields:

- `permission_id`
- `title`
- `description`
- `method`
- `url`
- `category`
- `created_at`
- `updated_at`

Each row represents one API access rule.

#### `roles`

Defined by `RoleEntity`.

Main fields:

- `role_id`
- `name_role`

Relations:

- many-to-many with `permissions` through `role_permissions`
- one-to-many with `user_roles`

#### `role_permissions`

Declared by `@JoinTable` in `RoleEntity`.

Columns:

- `role_id`
- `permission_id`

#### `user_roles`

Defined by `UserRoleEntity`.

Main fields:

- `id`
- `name`
- `description`
- `user_id`
- `role_id`

Constraint:

- unique `(role_id, user_id)`

### Roles In Code

Declared in `UserType`:

- `ADMIN`
- `CADMIN`
- `GUEST`
- `RCP`
- `USER`

### Permission Check Flow

Actual flow:

1. `JwtFilter` decides whether the request is public.
2. If not public, it reads JWT from `Authorization`.
3. It resolves the current user.
4. `PermissionRepository.findAllByUserId(...)` loads permissions from roles.
5. `PermissionServiceImpl.hasPermission(...)` matches:
   - HTTP method
   - URL pattern using `AntPathMatcher`
6. If there is no match, the API returns `403 Forbidden`.

## Current Caveats

- Migrations live in two directories (`db/migration` and `dev/db/migration`). The second is legacy but still active; consolidate into one location on a future cleanup pass.
- `application-secret.yml` is empty in the repo, while `application-dev.yml` still ships with committed credentials and API keys. Move sensitive values into `application-secret.yml` or environment variables instead of extending the committed-credentials pattern.
- `PermissionServiceImpl` caches permissions in memory and does not evict on role/permission changes — restart the app after editing role/permission assignments.
