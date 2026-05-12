# RBAC rollout guide

This repo already has JWT authentication and URL based authorization in code, but it is not fully enforced by default.

## What this seed gives you

- Base roles: `ADMIN`, `CADMIN`, `RCP`, `USER`, `GUEST`
- Practical permission groups by route family and HTTP method
- A starting role-permission matrix that fits the current controllers

The SQL seed is in [rbac-seed.sql](/D:/Workspace/poly-cinemas-dev/cineman/docs/rbac-seed.sql).

## Rollout order

1. Keep authorization disabled during bootstrap.
   - Current flag is `security.authorization.enabled: false` in [application.yml](/D:/Workspace/poly-cinemas-dev/cineman/src/main/resources/application.yml#L27).
   - If you turn it on before seeding roles and creating the first admin, you can lock yourself out.

2. Apply schema/migrations first.
   - Make sure the `roles`, `permissions`, `role_permissions`, `user_roles`, and `users` tables already exist.

3. Run the RBAC seed.
   - Execute [rbac-seed.sql](/D:/Workspace/poly-cinemas-dev/cineman/docs/rbac-seed.sql) against the target PostgreSQL database.

4. Create the first admin user while authorization is still off.
   - Use `POST /api/v01/admin/user/add`
   - Include `"roleIds": ["ADMIN"]`

Example request body:

```json
{
  "email": "admin@cineman.local",
  "fullName": "System Admin",
  "password": "ChangeMe123!",
  "phoneNumber": "0123456789",
  "address": "HQ",
  "dateOfBirth": "1990-01-01",
  "gender": "MALE",
  "roleIds": ["ADMIN"]
}
```

5. Log in with the admin account and verify access.
   - `POST /api/v01/auth/login`
   - Call one admin route such as `GET /api/v01/admin/role/all`

6. Turn authorization on and restart the app.
   - Change `security.authorization.enabled` to `true`
   - Restart the service

7. Run smoke tests.
   - No token -> private API should return `401`
   - Token without permission -> private API should return `403`
   - Token with permission -> API should succeed

## Current role design in this seed

- `ADMIN`
  - Full backoffice and customer route coverage from this seed
- `CADMIN`
  - Cinema setup, catalog, promotions, storage, daily operation routes
  - Does not get role, permission, or admin user management
- `RCP`
  - Front desk operations: invoice, ticket, snack booking, payment, QR, point history
- `USER`
  - Customer self-service: profile, booking, payment, promotions, point history, reviews
- `GUEST`
  - No explicit permissions
  - Public access is handled by bypass rules in [JwtFilter.java](/D:/Workspace/poly-cinemas-dev/cineman/src/main/java/com/codebloom/cineman/config/JwtFilter.java#L170)

## Important caveats before production

- Authorization is enforced by `JwtFilter`, not by `authorizeHttpRequests`.
  - See [SecurityConfiguration.java](/D:/Workspace/poly-cinemas-dev/cineman/src/main/java/com/codebloom/cineman/config/SecurityConfiguration.java#L55)
  - See [JwtFilter.java](/D:/Workspace/poly-cinemas-dev/cineman/src/main/java/com/codebloom/cineman/config/JwtFilter.java#L124)

- Permission checks are route based, not row based.
  - A user with `GET /api/v01/invoice/**` can call any matching invoice URL unless service code also verifies ownership.

- Permission cache is not invalidated after role/permission changes.
  - See [PermissionServiceImpl.java](/D:/Workspace/poly-cinemas-dev/cineman/src/main/java/com/codebloom/cineman/service/impl/PermissionServiceImpl.java#L117)
  - After changing role-permission assignments, restart the app or add cache eviction logic.

- `permissions.method` uses explicit integer codes.
  - See [PermissionEntity.java](/D:/Workspace/poly-cinemas-dev/cineman/src/main/java/com/codebloom/cineman/model/PermissionEntity.java#L34)
  - Current mapping is:
    - `GET=0`
    - `POST=1`
    - `PUT=2`
    - `PATCH=3`
    - `DELETE=4`
    - `OPTIONS=5`
  - The stored codes are stable even if the enum declaration order changes.

## Suggested next cleanup after rollout

1. Add cache eviction when role or permission data changes
2. Consider moving permission matching to `EnumType.STRING` for `Method` if you want DB readability over compact integer storage
3. Add service-level ownership checks for customer routes
