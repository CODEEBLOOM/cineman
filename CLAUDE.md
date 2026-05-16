# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Stack

Java 21, Spring Boot 3.5, Spring Data JPA + Hibernate, Spring Security + JWT, PostgreSQL, Flyway, WebSocket, springdoc-openapi, Lombok, ModelMapper, jjwt 0.11.5, Google API clients (Drive/People for OAuth + uploads), ZXing (QR codes), VNPay (sandbox).

Entry point: `src/main/java/com/codebloom/cineman/CinemanApplication.java`.

## Common commands (PowerShell from repo root)

```powershell
.\mvnw.cmd spring-boot:run                       # run with default profile (dev)
.\mvnw.cmd clean package                         # build cineman.jar (jar lands in target\cineman.jar)
java -jar target\cineman.jar
.\mvnw.cmd -P test test                          # full test suite, "test" maven profile (uses application-test.yml)
.\mvnw.cmd test -Dtest=ClassName                 # one test class
.\mvnw.cmd test -Dtest=ClassName#method          # one test method
docker compose up --build                        # full stack: PostgreSQL + backend (profiles dev,docker)
```

Maven profiles (`dev`, `test`, `prod`) set `spring.profiles.active` via the `@spring.profiles.active@` placeholder in `application.yml` — switch profile with `-P test` / `-P prod`, not `-Dspring.profiles.active`.

After startup: API at `http://localhost:8081/api/v01`, Swagger UI at `http://localhost:8081/swagger-ui/index.html`.

## Database setup

`application-dev.yml` expects PostgreSQL on `localhost:5432` with database `cineman`, user `java`, password `123456` (test profile uses DB `cineman_test`). Docker compose uses user/pass `postgres`/`postgres` instead — credentials are injected via env vars in `docker-compose.yml`.

### Flyway

Flyway is **enabled and authoritative** for schema. `ddl-auto: none` on dev — schema changes go through migrations, never through Hibernate. `config/FlywayConfiguration.java` manually wires a `Flyway` bean and calls `flyway.migrate()` on startup (gated by `@ConditionalOnProperty(spring.flyway.enabled=true)`).

Two migration locations are scanned (`spring.flyway.locations` in `application-dev.yml`):
- `src/main/resources/dev/db/migration/` — `V1`..`V6` (older legacy scripts, all PostgreSQL-compatible).
- `src/main/resources/db/migration/` — `V7`..onwards (active migrations).

When adding a migration, put it in `db/migration/` and bump to the next `V{n}__snake_case_name.sql`. Use idempotent PostgreSQL syntax (`IF EXISTS` / `IF NOT EXISTS`) to match the existing style (see `V11`, `V16`). After restart, verify via `SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC`.

`baseline-on-migrate: true` is set, so Flyway will baseline an existing DB at version 0 on first run rather than failing — but if a DB already has tables that conflict with `V1`, you may need to manually adjust `flyway_schema_history` or set `baseline-version`.

### Secrets

`src/main/resources/application-secret.yml` is empty in the repo and is the intended place for local-only secret overrides; the dev yaml currently contains committed credentials and API keys — do not extend that pattern, prefer `application-secret.yml` or env vars.

## Architecture: request flow and authorization

Layered: `controller → service (+ service/impl) → repository → model (JPA entities)`. DTOs live in `controller/request` and `controller/response`. Admin-only controllers are under `controller/admin`; auth endpoints under `controller/auth`.

Authorization is **not** done through Spring's `authorizeHttpRequests` — that chain is `permitAll()` in `SecurityConfiguration.java`. All access control runs inside `config/JwtFilter.java`:

1. `isBypassToken(request)` checks a hardcoded list of public routes (login/register/forgot-password, public movie/show-time/storage GETs, swagger). **To make an endpoint public you must add it to this list** — adding a controller alone is not enough.
2. Otherwise, JWT is validated and `PermissionService.hasPermission(userId, Method, requestURI)` matches the user's role-derived permissions against the request via `AntPathMatcher`.
3. Enforcement is gated by `security.authorization.enabled` in `application.yml`. It defaults to `false` — when off, the filter still authenticates if a token is present but skips the permission check. Turn it on only after seeding RBAC and creating the first admin (see `docs/rbac-rollout.md`).

RBAC tables: `users` → `user_roles` → `roles` ↔ `role_permissions` ↔ `permissions`. A `permissions` row pairs an HTTP method with a URL pattern; granting access is route-level only — service code must add ownership checks for per-row authorization.

### Two non-obvious gotchas

- `common/enums/Method.java` stores **explicit integer codes** in the DB (`GET=0, POST=1, PUT=2, PATCH=3, DELETE=4, OPTIONS=5`). Do not reorder enum constants without a data migration; codes are persisted via a converter and reordering will silently corrupt permission rows.
- `PermissionServiceImpl` caches permissions and **does not evict on role/permission changes**. After editing role-permission assignments you must restart the app (or add eviction) for changes to take effect.

## Roles

Declared in `common/enums/UserType.java`: `ADMIN`, `CADMIN` (cinema admin), `RCP` (reception/front desk), `USER`, `GUEST`. `GUEST` has no DB permissions — public access flows through the `JwtFilter` bypass list. The starting role/permission matrix is in `docs/rbac-seed.sql`; rollout procedure is in `docs/rbac-rollout.md`.

## Auxiliary config worth knowing

- `config/StaticResourcesWebConfig.java` serves uploaded files from `cinema.upload_file.base_path` (a `file://` URL — dev points at a local Windows path, docker maps `/app/uploads` volume).
- `config/WebSocketConfiguration.java` + `WebSocketAuthChannelInterceptor.java` wire STOMP with JWT auth on the CONNECT frame; controllers under `controller/websocket`.
- `config/GoogleDriveConfig.java` loads `service_account.json` from classpath (committed in `src/main/resources/`) for Drive uploads.
- VNPay sandbox is the configured payment gateway; keys live in `application-dev.yml` under `vnpay.*`.

## Reference docs already in the repo

- `README.md` — onboarding overview
- `Structure.md` — package responsibilities, request flow, RBAC tables (more detailed than this file)
- `docs/rbac-rollout.md` — step-by-step procedure for turning authorization on
- `docs/rbac-seed.sql` — base roles/permissions seed
- `docs/showtime-flow-analysis.md` — data dependencies for creating showtimes