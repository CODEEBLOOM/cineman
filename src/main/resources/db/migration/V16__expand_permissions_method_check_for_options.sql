ALTER TABLE permissions
    DROP CONSTRAINT IF EXISTS permissions_method_check;

ALTER TABLE permissions
    ADD CONSTRAINT permissions_method_check
        CHECK (method IN (0, 1, 2, 3, 4, 5));
