DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'profiles'
          AND column_name = 'surname'
    ) THEN
ALTER TABLE profiles
    ALTER COLUMN surname DROP NOT NULL;
END IF;
END $$;
