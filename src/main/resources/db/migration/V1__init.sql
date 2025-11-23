
CREATE TABLE IF NOT EXISTS users (
                                     id            BIGSERIAL PRIMARY KEY,
                                     email         VARCHAR(255) NOT NULL,
                                     password_hash VARCHAR(255) NOT NULL,
                                     role          VARCHAR(20)  NOT NULL,
                                     created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);


ALTER TABLE users ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255);
ALTER TABLE users ALTER COLUMN password_hash SET DEFAULT 'dev_password';
UPDATE users SET password_hash = 'dev_password' WHERE password_hash IS NULL;
ALTER TABLE users ALTER COLUMN password_hash SET NOT NULL;
ALTER TABLE users ALTER COLUMN password_hash DROP DEFAULT;


ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(20);
ALTER TABLE users ALTER COLUMN role SET DEFAULT 'WORKER';
UPDATE users SET role = 'WORKER' WHERE role IS NULL;
ALTER TABLE users ALTER COLUMN role SET NOT NULL;
ALTER TABLE users ALTER COLUMN role DROP DEFAULT;


ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ;
ALTER TABLE users ALTER COLUMN created_at SET DEFAULT NOW();
UPDATE users SET created_at = NOW() WHERE created_at IS NULL;
ALTER TABLE users ALTER COLUMN created_at SET NOT NULL;


CREATE UNIQUE INDEX IF NOT EXISTS uq_users_email ON users(email);



CREATE TABLE IF NOT EXISTS profiles (
                                        user_id    BIGINT      PRIMARY KEY,
                                        name       VARCHAR(100) NOT NULL,
                                        surname    VARCHAR(100) NOT NULL,
                                        phone      VARCHAR(50),
                                        city       VARCHAR(100),
                                        categories TEXT[]
);


DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint
            WHERE  conname = 'fk_profiles_user'
              AND    conrelid = 'profiles'::regclass
        ) THEN
            ALTER TABLE profiles
                ADD CONSTRAINT fk_profiles_user
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
        END IF;
    END$$;


CREATE TABLE IF NOT EXISTS jobs (
                                    id           BIGSERIAL PRIMARY KEY,
                                    employer_id  BIGINT       NOT NULL,
                                    title        VARCHAR(200) NOT NULL,
                                    description  TEXT,
                                    category     VARCHAR(20)  NOT NULL,
                                    city         VARCHAR(100) NOT NULL,
                                    price        NUMERIC(10,2) NOT NULL,
                                    status       VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
                                    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);


DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint
            WHERE  conname = 'fk_jobs_employer'
              AND    conrelid = 'jobs'::regclass
        ) THEN
            ALTER TABLE jobs
                ADD CONSTRAINT fk_jobs_employer
                    FOREIGN KEY (employer_id) REFERENCES users(id);
        END IF;
    END$$;


CREATE INDEX IF NOT EXISTS idx_jobs_created_at                 ON jobs(created_at);
CREATE INDEX IF NOT EXISTS idx_jobs_city_category_created_at   ON jobs(city, category, created_at);
