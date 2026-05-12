-- RBAC seed for Cineman
-- Target DB: PostgreSQL
-- Notes:
-- 1. Permission.method uses explicit integer codes in code:
--    GET=0, POST=1, PUT=2, PATCH=3, DELETE=4, OPTIONS=5
-- 2. This seed is intentionally broad by route group to make rollout practical.
-- 3. Public endpoints listed in JwtFilter bypass do not need permission rows.

BEGIN;

INSERT INTO roles (role_id, name_role, status, created_at, updated_at)
VALUES
    ('ADMIN', 'Administrator', TRUE, NOW(), NOW()),
    ('CADMIN', 'Cinema Admin', TRUE, NOW(), NOW()),
    ('RCP', 'Receptionist', TRUE, NOW(), NOW()),
    ('USER', 'User', TRUE, NOW(), NOW()),
    ('GUEST', 'Guest', TRUE, NOW(), NOW())
ON CONFLICT (role_id) DO UPDATE
SET
    name_role = EXCLUDED.name_role,
    status = TRUE,
    updated_at = NOW();

WITH seed_permissions (title, description, method, url, category) AS (
    VALUES
        ('SYS_ROLE_GET', 'Read role configuration', 0, '/api/v01/admin/role/**', 'system'),
        ('SYS_ROLE_POST', 'Create role configuration', 1, '/api/v01/admin/role/**', 'system'),
        ('SYS_ROLE_PUT', 'Update role configuration', 2, '/api/v01/admin/role/**', 'system'),
        ('SYS_ROLE_PATCH', 'Patch role configuration', 3, '/api/v01/admin/role/**', 'system'),
        ('SYS_ROLE_DELETE', 'Delete role configuration', 4, '/api/v01/admin/role/**', 'system'),

        ('SYS_PERMISSION_GET', 'Read permission configuration', 0, '/api/v01/admin/permissions/**', 'system'),
        ('SYS_PERMISSION_POST', 'Create permission configuration', 1, '/api/v01/admin/permissions/**', 'system'),
        ('SYS_PERMISSION_PUT', 'Update permission configuration', 2, '/api/v01/admin/permissions/**', 'system'),
        ('SYS_PERMISSION_DELETE', 'Delete permission configuration', 4, '/api/v01/admin/permissions/**', 'system'),

        ('SYS_USER_GET', 'Read backoffice users', 0, '/api/v01/admin/user/**', 'system'),
        ('SYS_USER_POST', 'Create backoffice users', 1, '/api/v01/admin/user/**', 'system'),
        ('SYS_USER_PUT', 'Update backoffice users', 2, '/api/v01/admin/user/**', 'system'),
        ('SYS_USER_DELETE', 'Delete backoffice users', 4, '/api/v01/admin/user/**', 'system'),

        ('ADMIN_MOVIE_GET', 'Read movie admin routes', 0, '/api/v01/admin/mov*/**', 'catalog'),
        ('ADMIN_MOVIE_POST', 'Create movie admin routes', 1, '/api/v01/admin/mov*/**', 'catalog'),
        ('ADMIN_MOVIE_PUT', 'Update movie admin routes', 2, '/api/v01/admin/mov*/**', 'catalog'),
        ('ADMIN_MOVIE_DELETE', 'Delete movie admin routes', 4, '/api/v01/admin/mov*/**', 'catalog'),

        ('ADMIN_CINEMA_GET', 'Read cinema admin routes', 0, '/api/v01/admin/cinema*/**', 'cinema'),
        ('ADMIN_CINEMA_POST', 'Create cinema admin routes', 1, '/api/v01/admin/cinema*/**', 'cinema'),
        ('ADMIN_CINEMA_PUT', 'Update cinema admin routes', 2, '/api/v01/admin/cinema*/**', 'cinema'),
        ('ADMIN_CINEMA_DELETE', 'Delete cinema admin routes', 4, '/api/v01/admin/cinema*/**', 'cinema'),

        ('ADMIN_SEAT_GET', 'Read seat admin routes', 0, '/api/v01/admin/seat*/**', 'cinema'),
        ('ADMIN_SEAT_POST', 'Create seat admin routes', 1, '/api/v01/admin/seat*/**', 'cinema'),
        ('ADMIN_SEAT_PUT', 'Update seat admin routes', 2, '/api/v01/admin/seat*/**', 'cinema'),
        ('ADMIN_SEAT_DELETE', 'Delete seat admin routes', 4, '/api/v01/admin/seat*/**', 'cinema'),

        ('ADMIN_SNACK_GET', 'Read snack admin routes', 0, '/api/v01/admin/snack*/**', 'catalog'),
        ('ADMIN_SNACK_POST', 'Create snack admin routes', 1, '/api/v01/admin/snack*/**', 'catalog'),
        ('ADMIN_SNACK_PUT', 'Update snack admin routes', 2, '/api/v01/admin/snack*/**', 'catalog'),
        ('ADMIN_SNACK_DELETE', 'Delete snack admin routes', 4, '/api/v01/admin/snack*/**', 'catalog'),

        ('ADMIN_PROMOTION_GET', 'Read promotion admin routes', 0, '/api/v01/admin/promotion*/**', 'promotion'),
        ('ADMIN_PROMOTION_POST', 'Create promotion admin routes', 1, '/api/v01/admin/promotion*/**', 'promotion'),
        ('ADMIN_PROMOTION_PUT', 'Update promotion admin routes', 2, '/api/v01/admin/promotion*/**', 'promotion'),
        ('ADMIN_PROMOTION_DELETE', 'Delete promotion admin routes', 4, '/api/v01/admin/promotion*/**', 'promotion'),

        ('ADMIN_TICKET_TYPE_GET', 'Read ticket type admin routes', 0, '/api/v01/admin/ticket-type/**', 'catalog'),
        ('ADMIN_TICKET_TYPE_POST', 'Create ticket type admin routes', 1, '/api/v01/admin/ticket-type/**', 'catalog'),
        ('ADMIN_TICKET_TYPE_PUT', 'Update ticket type admin routes', 2, '/api/v01/admin/ticket-type/**', 'catalog'),
        ('ADMIN_TICKET_TYPE_DELETE', 'Delete ticket type admin routes', 4, '/api/v01/admin/ticket-type/**', 'catalog'),

        ('ADMIN_INVOICE_GET', 'Read admin invoice routes', 0, '/api/v01/admin/invoice/**', 'operation'),
        ('ADMIN_INVOICE_PUT', 'Update admin invoice routes', 2, '/api/v01/admin/invoice/**', 'operation'),

        ('ADMIN_PROVINCE_GET', 'Read province admin routes', 0, '/api/v01/admin/province/**', 'reference'),
        ('ADMIN_PROVINCE_POST', 'Create province admin routes', 1, '/api/v01/admin/province/**', 'reference'),
        ('ADMIN_PROVINCE_PUT', 'Update province admin routes', 2, '/api/v01/admin/province/**', 'reference'),
        ('ADMIN_PROVINCE_DELETE', 'Delete province admin routes', 4, '/api/v01/admin/province/**', 'reference'),

        ('ADMIN_PARTICIPANT_GET', 'Read participant admin routes', 0, '/api/v01/admin/participant/**', 'catalog'),
        ('ADMIN_PARTICIPANT_POST', 'Create participant admin routes', 1, '/api/v01/admin/participant/**', 'catalog'),
        ('ADMIN_PARTICIPANT_PUT', 'Update participant admin routes', 2, '/api/v01/admin/participant/**', 'catalog'),
        ('ADMIN_PARTICIPANT_DELETE', 'Delete participant admin routes', 4, '/api/v01/admin/participant/**', 'catalog'),

        ('ADMIN_MOVIE_PARTICIPANT_GET', 'Read movie participant admin routes', 0, '/api/v01/admin/movie-participant/**', 'catalog'),
        ('ADMIN_MOVIE_PARTICIPANT_POST', 'Create movie participant admin routes', 1, '/api/v01/admin/movie-participant/**', 'catalog'),
        ('ADMIN_MOVIE_PARTICIPANT_PUT', 'Update movie participant admin routes', 2, '/api/v01/admin/movie-participant/**', 'catalog'),
        ('ADMIN_MOVIE_PARTICIPANT_DELETE', 'Delete movie participant admin routes', 4, '/api/v01/admin/movie-participant/**', 'catalog'),

        ('ADMIN_MISC_GET', 'Read admin membership rank and related routes', 0, '/api/v01/admin/membership-rank/**', 'reference'),
        ('ADMIN_MISC_POST', 'Create admin membership rank and related routes', 1, '/api/v01/admin/membership-rank/**', 'reference'),
        ('ADMIN_MISC_PUT', 'Update admin membership rank and related routes', 2, '/api/v01/admin/membership-rank/**', 'reference'),
        ('ADMIN_MISC_DELETE', 'Delete admin membership rank and related routes', 4, '/api/v01/admin/membership-rank/**', 'reference'),

        ('FILE_STORAGE_GET', 'Read local file storage routes', 0, '/api/v01/files/**', 'storage'),
        ('FILE_STORAGE_POST', 'Write local file storage routes', 1, '/api/v01/files/**', 'storage'),
        ('FILE_STORAGE_DELETE', 'Delete local file storage routes', 4, '/api/v01/files/**', 'storage'),

        ('DRIVE_GET', 'Read Google Drive routes', 0, '/api/v01/drive/**', 'storage'),
        ('DRIVE_POST', 'Write Google Drive routes', 1, '/api/v01/drive/**', 'storage'),
        ('DRIVE_DELETE', 'Delete Google Drive routes', 4, '/api/v01/drive/**', 'storage'),

        ('REF_MEMBERSHIP_GET', 'Read customer membership rank routes', 0, '/api/v01/membership-rank/**', 'customer-reference'),
        ('REF_SNACK_GET', 'Read customer snack routes', 0, '/api/v01/snack/**', 'customer-reference'),
        ('REF_SNACK_TYPE_GET', 'Read customer snack type routes', 0, '/api/v01/snack-type/**', 'customer-reference'),
        ('REF_TICKET_TYPE_GET', 'Read customer ticket type routes', 0, '/api/v01/ticket-type/**', 'customer-reference'),
        ('REF_MOVIE_VARIATION_GET', 'Read customer movie variation routes', 0, '/api/v01/movie-variation/**', 'customer-reference'),

        ('AUTH_PROFILE_GET', 'Read current auth profile', 0, '/api/v01/auth/user', 'customer-account'),
        ('AUTH_PASSWORD_PUT', 'Change own password', 2, '/api/v01/auth/change-pwd', 'customer-account'),
        ('CUSTOMER_PROFILE_POST', 'Use customer point conversion routes', 1, '/api/v01/user/**', 'customer-account'),
        ('CUSTOMER_PROFILE_PUT', 'Update own customer profile routes', 2, '/api/v01/user/**', 'customer-account'),

        ('BOOKING_INVOICE_GET', 'Read booking invoice routes', 0, '/api/v01/invoice/**', 'booking'),
        ('BOOKING_INVOICE_POST', 'Create booking invoice routes', 1, '/api/v01/invoice/**', 'booking'),
        ('BOOKING_INVOICE_PUT', 'Update booking invoice routes', 2, '/api/v01/invoice/**', 'booking'),

        ('BOOKING_TICKET_GET', 'Read booking ticket routes', 0, '/api/v01/ticket/**', 'booking'),
        ('BOOKING_TICKET_POST', 'Create booking ticket routes', 1, '/api/v01/ticket/**', 'booking'),
        ('BOOKING_TICKET_PUT', 'Update booking ticket routes', 2, '/api/v01/ticket/**', 'booking'),

        ('BOOKING_SNACK_POST', 'Create booking snack routes', 1, '/api/v01/detail-booking-snack/**', 'booking'),
        ('BOOKING_SNACK_PUT', 'Update booking snack routes', 2, '/api/v01/detail-booking-snack/**', 'booking'),
        ('BOOKING_SNACK_DELETE', 'Delete booking snack routes', 4, '/api/v01/detail-booking-snack/**', 'booking'),

        ('PAYMENT_POST', 'Create payment routes', 1, '/api/v01/payment/**', 'payment'),

        ('PROMOTION_CUSTOMER_GET', 'Read customer promotion routes', 0, '/api/v01/promotion/**', 'promotion'),
        ('PROMOTION_CUSTOMER_PUT', 'Apply or revert customer promotion routes', 2, '/api/v01/promotion/**', 'promotion'),

        ('POINT_HISTORY_GET', 'Read user point history routes', 0, '/api/v01/user-point-history/**', 'reward'),
        ('POINT_HISTORY_POST', 'Create user point history routes', 1, '/api/v01/user-point-history/**', 'reward'),

        ('MOVIE_REVIEW_GET', 'Read movie review routes', 0, '/api/v01/movie/*/reviews/**', 'review'),
        ('MOVIE_REVIEW_POST', 'Create movie review routes', 1, '/api/v01/movie/*/reviews/**', 'review'),
        ('MOVIE_REVIEW_PUT', 'Update movie review routes', 2, '/api/v01/movie/*/reviews/**', 'review'),
        ('MOVIE_REVIEW_DELETE', 'Delete movie review routes', 4, '/api/v01/movie/*/reviews/**', 'review'),

        ('QR_CODE_POST', 'Create QR code routes', 1, '/api/v01/qrcode/**', 'utility')
)
INSERT INTO permissions (title, description, method, url, category, created_at, updated_at)
SELECT
    sp.title,
    sp.description,
    sp.method,
    sp.url,
    sp.category,
    NOW(),
    NOW()
FROM seed_permissions sp
WHERE NOT EXISTS (
    SELECT 1
    FROM permissions p
    WHERE p.title = sp.title
      AND p.method = sp.method
      AND p.url = sp.url
);

WITH seed_role_permissions (role_id, permission_title) AS (
    VALUES
        -- ADMIN: everything in this seed
        ('ADMIN', 'SYS_ROLE_GET'),
        ('ADMIN', 'SYS_ROLE_POST'),
        ('ADMIN', 'SYS_ROLE_PUT'),
        ('ADMIN', 'SYS_ROLE_PATCH'),
        ('ADMIN', 'SYS_ROLE_DELETE'),
        ('ADMIN', 'SYS_PERMISSION_GET'),
        ('ADMIN', 'SYS_PERMISSION_POST'),
        ('ADMIN', 'SYS_PERMISSION_PUT'),
        ('ADMIN', 'SYS_PERMISSION_DELETE'),
        ('ADMIN', 'SYS_USER_GET'),
        ('ADMIN', 'SYS_USER_POST'),
        ('ADMIN', 'SYS_USER_PUT'),
        ('ADMIN', 'SYS_USER_DELETE'),
        ('ADMIN', 'ADMIN_MOVIE_GET'),
        ('ADMIN', 'ADMIN_MOVIE_POST'),
        ('ADMIN', 'ADMIN_MOVIE_PUT'),
        ('ADMIN', 'ADMIN_MOVIE_DELETE'),
        ('ADMIN', 'ADMIN_CINEMA_GET'),
        ('ADMIN', 'ADMIN_CINEMA_POST'),
        ('ADMIN', 'ADMIN_CINEMA_PUT'),
        ('ADMIN', 'ADMIN_CINEMA_DELETE'),
        ('ADMIN', 'ADMIN_SEAT_GET'),
        ('ADMIN', 'ADMIN_SEAT_POST'),
        ('ADMIN', 'ADMIN_SEAT_PUT'),
        ('ADMIN', 'ADMIN_SEAT_DELETE'),
        ('ADMIN', 'ADMIN_SNACK_GET'),
        ('ADMIN', 'ADMIN_SNACK_POST'),
        ('ADMIN', 'ADMIN_SNACK_PUT'),
        ('ADMIN', 'ADMIN_SNACK_DELETE'),
        ('ADMIN', 'ADMIN_PROMOTION_GET'),
        ('ADMIN', 'ADMIN_PROMOTION_POST'),
        ('ADMIN', 'ADMIN_PROMOTION_PUT'),
        ('ADMIN', 'ADMIN_PROMOTION_DELETE'),
        ('ADMIN', 'ADMIN_TICKET_TYPE_GET'),
        ('ADMIN', 'ADMIN_TICKET_TYPE_POST'),
        ('ADMIN', 'ADMIN_TICKET_TYPE_PUT'),
        ('ADMIN', 'ADMIN_TICKET_TYPE_DELETE'),
        ('ADMIN', 'ADMIN_INVOICE_GET'),
        ('ADMIN', 'ADMIN_INVOICE_PUT'),
        ('ADMIN', 'ADMIN_PROVINCE_GET'),
        ('ADMIN', 'ADMIN_PROVINCE_POST'),
        ('ADMIN', 'ADMIN_PROVINCE_PUT'),
        ('ADMIN', 'ADMIN_PROVINCE_DELETE'),
        ('ADMIN', 'ADMIN_PARTICIPANT_GET'),
        ('ADMIN', 'ADMIN_PARTICIPANT_POST'),
        ('ADMIN', 'ADMIN_PARTICIPANT_PUT'),
        ('ADMIN', 'ADMIN_PARTICIPANT_DELETE'),
        ('ADMIN', 'ADMIN_MOVIE_PARTICIPANT_GET'),
        ('ADMIN', 'ADMIN_MOVIE_PARTICIPANT_POST'),
        ('ADMIN', 'ADMIN_MOVIE_PARTICIPANT_PUT'),
        ('ADMIN', 'ADMIN_MOVIE_PARTICIPANT_DELETE'),
        ('ADMIN', 'ADMIN_MISC_GET'),
        ('ADMIN', 'ADMIN_MISC_POST'),
        ('ADMIN', 'ADMIN_MISC_PUT'),
        ('ADMIN', 'ADMIN_MISC_DELETE'),
        ('ADMIN', 'FILE_STORAGE_GET'),
        ('ADMIN', 'FILE_STORAGE_POST'),
        ('ADMIN', 'FILE_STORAGE_DELETE'),
        ('ADMIN', 'DRIVE_GET'),
        ('ADMIN', 'DRIVE_POST'),
        ('ADMIN', 'DRIVE_DELETE'),
        ('ADMIN', 'REF_MEMBERSHIP_GET'),
        ('ADMIN', 'REF_SNACK_GET'),
        ('ADMIN', 'REF_SNACK_TYPE_GET'),
        ('ADMIN', 'REF_TICKET_TYPE_GET'),
        ('ADMIN', 'REF_MOVIE_VARIATION_GET'),
        ('ADMIN', 'AUTH_PROFILE_GET'),
        ('ADMIN', 'AUTH_PASSWORD_PUT'),
        ('ADMIN', 'CUSTOMER_PROFILE_POST'),
        ('ADMIN', 'CUSTOMER_PROFILE_PUT'),
        ('ADMIN', 'BOOKING_INVOICE_GET'),
        ('ADMIN', 'BOOKING_INVOICE_POST'),
        ('ADMIN', 'BOOKING_INVOICE_PUT'),
        ('ADMIN', 'BOOKING_TICKET_GET'),
        ('ADMIN', 'BOOKING_TICKET_POST'),
        ('ADMIN', 'BOOKING_TICKET_PUT'),
        ('ADMIN', 'BOOKING_SNACK_POST'),
        ('ADMIN', 'BOOKING_SNACK_PUT'),
        ('ADMIN', 'BOOKING_SNACK_DELETE'),
        ('ADMIN', 'PAYMENT_POST'),
        ('ADMIN', 'PROMOTION_CUSTOMER_GET'),
        ('ADMIN', 'PROMOTION_CUSTOMER_PUT'),
        ('ADMIN', 'POINT_HISTORY_GET'),
        ('ADMIN', 'POINT_HISTORY_POST'),
        ('ADMIN', 'MOVIE_REVIEW_GET'),
        ('ADMIN', 'MOVIE_REVIEW_POST'),
        ('ADMIN', 'MOVIE_REVIEW_PUT'),
        ('ADMIN', 'MOVIE_REVIEW_DELETE'),
        ('ADMIN', 'QR_CODE_POST'),

        -- CADMIN: cinema operations, catalog, storage, and daily workflows
        ('CADMIN', 'ADMIN_MOVIE_GET'),
        ('CADMIN', 'ADMIN_MOVIE_POST'),
        ('CADMIN', 'ADMIN_MOVIE_PUT'),
        ('CADMIN', 'ADMIN_MOVIE_DELETE'),
        ('CADMIN', 'ADMIN_CINEMA_GET'),
        ('CADMIN', 'ADMIN_CINEMA_POST'),
        ('CADMIN', 'ADMIN_CINEMA_PUT'),
        ('CADMIN', 'ADMIN_CINEMA_DELETE'),
        ('CADMIN', 'ADMIN_SEAT_GET'),
        ('CADMIN', 'ADMIN_SEAT_POST'),
        ('CADMIN', 'ADMIN_SEAT_PUT'),
        ('CADMIN', 'ADMIN_SEAT_DELETE'),
        ('CADMIN', 'ADMIN_SNACK_GET'),
        ('CADMIN', 'ADMIN_SNACK_POST'),
        ('CADMIN', 'ADMIN_SNACK_PUT'),
        ('CADMIN', 'ADMIN_SNACK_DELETE'),
        ('CADMIN', 'ADMIN_PROMOTION_GET'),
        ('CADMIN', 'ADMIN_PROMOTION_POST'),
        ('CADMIN', 'ADMIN_PROMOTION_PUT'),
        ('CADMIN', 'ADMIN_PROMOTION_DELETE'),
        ('CADMIN', 'ADMIN_TICKET_TYPE_GET'),
        ('CADMIN', 'ADMIN_TICKET_TYPE_POST'),
        ('CADMIN', 'ADMIN_TICKET_TYPE_PUT'),
        ('CADMIN', 'ADMIN_TICKET_TYPE_DELETE'),
        ('CADMIN', 'ADMIN_INVOICE_GET'),
        ('CADMIN', 'ADMIN_INVOICE_PUT'),
        ('CADMIN', 'ADMIN_PROVINCE_GET'),
        ('CADMIN', 'ADMIN_PARTICIPANT_GET'),
        ('CADMIN', 'ADMIN_PARTICIPANT_POST'),
        ('CADMIN', 'ADMIN_PARTICIPANT_PUT'),
        ('CADMIN', 'ADMIN_PARTICIPANT_DELETE'),
        ('CADMIN', 'ADMIN_MOVIE_PARTICIPANT_GET'),
        ('CADMIN', 'ADMIN_MOVIE_PARTICIPANT_POST'),
        ('CADMIN', 'ADMIN_MOVIE_PARTICIPANT_PUT'),
        ('CADMIN', 'ADMIN_MOVIE_PARTICIPANT_DELETE'),
        ('CADMIN', 'ADMIN_MISC_GET'),
        ('CADMIN', 'ADMIN_MISC_POST'),
        ('CADMIN', 'ADMIN_MISC_PUT'),
        ('CADMIN', 'ADMIN_MISC_DELETE'),
        ('CADMIN', 'FILE_STORAGE_GET'),
        ('CADMIN', 'FILE_STORAGE_POST'),
        ('CADMIN', 'FILE_STORAGE_DELETE'),
        ('CADMIN', 'DRIVE_GET'),
        ('CADMIN', 'DRIVE_POST'),
        ('CADMIN', 'DRIVE_DELETE'),
        ('CADMIN', 'REF_MEMBERSHIP_GET'),
        ('CADMIN', 'REF_SNACK_GET'),
        ('CADMIN', 'REF_SNACK_TYPE_GET'),
        ('CADMIN', 'REF_TICKET_TYPE_GET'),
        ('CADMIN', 'REF_MOVIE_VARIATION_GET'),
        ('CADMIN', 'AUTH_PROFILE_GET'),
        ('CADMIN', 'AUTH_PASSWORD_PUT'),
        ('CADMIN', 'CUSTOMER_PROFILE_POST'),
        ('CADMIN', 'CUSTOMER_PROFILE_PUT'),
        ('CADMIN', 'BOOKING_INVOICE_GET'),
        ('CADMIN', 'BOOKING_INVOICE_POST'),
        ('CADMIN', 'BOOKING_INVOICE_PUT'),
        ('CADMIN', 'BOOKING_TICKET_GET'),
        ('CADMIN', 'BOOKING_TICKET_POST'),
        ('CADMIN', 'BOOKING_TICKET_PUT'),
        ('CADMIN', 'BOOKING_SNACK_POST'),
        ('CADMIN', 'BOOKING_SNACK_PUT'),
        ('CADMIN', 'BOOKING_SNACK_DELETE'),
        ('CADMIN', 'PAYMENT_POST'),
        ('CADMIN', 'PROMOTION_CUSTOMER_GET'),
        ('CADMIN', 'PROMOTION_CUSTOMER_PUT'),
        ('CADMIN', 'POINT_HISTORY_GET'),
        ('CADMIN', 'POINT_HISTORY_POST'),
        ('CADMIN', 'QR_CODE_POST'),

        -- RCP: front desk operations
        ('RCP', 'ADMIN_INVOICE_GET'),
        ('RCP', 'ADMIN_INVOICE_PUT'),
        ('RCP', 'REF_MEMBERSHIP_GET'),
        ('RCP', 'REF_SNACK_GET'),
        ('RCP', 'REF_SNACK_TYPE_GET'),
        ('RCP', 'REF_TICKET_TYPE_GET'),
        ('RCP', 'REF_MOVIE_VARIATION_GET'),
        ('RCP', 'AUTH_PROFILE_GET'),
        ('RCP', 'AUTH_PASSWORD_PUT'),
        ('RCP', 'CUSTOMER_PROFILE_POST'),
        ('RCP', 'CUSTOMER_PROFILE_PUT'),
        ('RCP', 'BOOKING_INVOICE_GET'),
        ('RCP', 'BOOKING_INVOICE_POST'),
        ('RCP', 'BOOKING_INVOICE_PUT'),
        ('RCP', 'BOOKING_TICKET_GET'),
        ('RCP', 'BOOKING_TICKET_POST'),
        ('RCP', 'BOOKING_TICKET_PUT'),
        ('RCP', 'BOOKING_SNACK_POST'),
        ('RCP', 'BOOKING_SNACK_PUT'),
        ('RCP', 'BOOKING_SNACK_DELETE'),
        ('RCP', 'PAYMENT_POST'),
        ('RCP', 'PROMOTION_CUSTOMER_GET'),
        ('RCP', 'PROMOTION_CUSTOMER_PUT'),
        ('RCP', 'POINT_HISTORY_GET'),
        ('RCP', 'POINT_HISTORY_POST'),
        ('RCP', 'QR_CODE_POST'),

        -- USER: customer self-service
        ('USER', 'REF_MEMBERSHIP_GET'),
        ('USER', 'REF_SNACK_GET'),
        ('USER', 'REF_SNACK_TYPE_GET'),
        ('USER', 'REF_TICKET_TYPE_GET'),
        ('USER', 'REF_MOVIE_VARIATION_GET'),
        ('USER', 'AUTH_PROFILE_GET'),
        ('USER', 'AUTH_PASSWORD_PUT'),
        ('USER', 'CUSTOMER_PROFILE_POST'),
        ('USER', 'CUSTOMER_PROFILE_PUT'),
        ('USER', 'BOOKING_INVOICE_GET'),
        ('USER', 'BOOKING_INVOICE_POST'),
        ('USER', 'BOOKING_INVOICE_PUT'),
        ('USER', 'BOOKING_TICKET_GET'),
        ('USER', 'BOOKING_TICKET_POST'),
        ('USER', 'BOOKING_TICKET_PUT'),
        ('USER', 'BOOKING_SNACK_POST'),
        ('USER', 'BOOKING_SNACK_PUT'),
        ('USER', 'BOOKING_SNACK_DELETE'),
        ('USER', 'PAYMENT_POST'),
        ('USER', 'PROMOTION_CUSTOMER_GET'),
        ('USER', 'PROMOTION_CUSTOMER_PUT'),
        ('USER', 'POINT_HISTORY_GET'),
        ('USER', 'POINT_HISTORY_POST'),
        ('USER', 'MOVIE_REVIEW_GET'),
        ('USER', 'MOVIE_REVIEW_POST'),
        ('USER', 'MOVIE_REVIEW_PUT'),
        ('USER', 'MOVIE_REVIEW_DELETE')
)
INSERT INTO role_permissions (role_id, permission_id)
SELECT
    srp.role_id,
    p.permission_id
FROM seed_role_permissions srp
JOIN permissions p
    ON p.title = srp.permission_title
WHERE NOT EXISTS (
    SELECT 1
    FROM role_permissions rp
    WHERE rp.role_id = srp.role_id
      AND rp.permission_id = p.permission_id
);

COMMIT;

-- Optional smoke checks:
-- SELECT role_id, name_role, status FROM roles ORDER BY role_id;
-- SELECT title, method, url, category FROM permissions ORDER BY category, title;
-- SELECT rp.role_id, p.title FROM role_permissions rp JOIN permissions p ON p.permission_id = rp.permission_id ORDER BY rp.role_id, p.title;
