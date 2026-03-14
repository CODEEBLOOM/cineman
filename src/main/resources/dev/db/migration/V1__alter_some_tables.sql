ALTER TABLE IF EXISTS show_times
    ALTER COLUMN origin_price TYPE NUMERIC(10,2),
    ALTER COLUMN origin_price SET NOT NULL;

DO $$
BEGIN
    IF to_regclass('show_times') IS NOT NULL
        AND NOT EXISTS (
            SELECT 1
            FROM pg_constraint
            WHERE conrelid = 'show_times'::regclass
              AND contype = 'c'
              AND pg_get_constraintdef(oid) ILIKE '%origin_price >= 0%'
        ) THEN
        ALTER TABLE show_times
            ADD CONSTRAINT ck_show_times_origin_price_non_negative
                CHECK (origin_price >= 0);
    END IF;
END $$;

ALTER TABLE IF EXISTS seat_types
    ALTER COLUMN price TYPE NUMERIC(10,2),
    ALTER COLUMN price SET NOT NULL;

DO $$
BEGIN
    IF to_regclass('seat_types') IS NOT NULL
        AND NOT EXISTS (
            SELECT 1
            FROM pg_constraint
            WHERE conrelid = 'seat_types'::regclass
              AND contype = 'c'
              AND pg_get_constraintdef(oid) ILIKE '%price >= 0%'
        ) THEN
        ALTER TABLE seat_types
            ADD CONSTRAINT ck_seat_types_price_non_negative
                CHECK (price >= 0);
    END IF;
END $$;

ALTER TABLE IF EXISTS seat_types
    ALTER COLUMN name TYPE VARCHAR(200),
    ALTER COLUMN name SET NOT NULL;