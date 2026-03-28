DO $$
DECLARE
    constraint_name TEXT;
BEGIN
    FOR constraint_name IN
        SELECT c.conname
        FROM pg_constraint c
        JOIN pg_class t
            ON t.oid = c.conrelid
        WHERE t.relname = 'invoices'
          AND c.contype = 'u'
          AND EXISTS (
              SELECT 1
              FROM unnest(c.conkey) AS column_id(attnum)
              JOIN pg_attribute a
                  ON a.attrelid = t.oid
                 AND a.attnum = column_id.attnum
              WHERE a.attname IN ('email', 'phone_number')
          )
    LOOP
        EXECUTE format('ALTER TABLE invoices DROP CONSTRAINT IF EXISTS %I', constraint_name);
    END LOOP;
END $$;
