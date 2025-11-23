ALTER TABLE profiles
    ADD COLUMN id BIGINT;

CREATE SEQUENCE profiles_id_seq
    OWNED BY profiles.id;


ALTER TABLE profiles
    ALTER COLUMN id SET DEFAULT nextval('profiles_id_seq');


UPDATE profiles
SET id = nextval('profiles_id_seq')
WHERE id IS NULL;


ALTER TABLE profiles
    ALTER COLUMN id SET NOT NULL;


ALTER TABLE profiles
DROP CONSTRAINT profiles_pkey;


ALTER TABLE profiles
    ADD CONSTRAINT profiles_pkey PRIMARY KEY (id);
