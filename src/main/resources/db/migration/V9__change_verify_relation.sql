ALTER TABLE app_user DROP COLUMN verification_id;

ALTER TABLE verification
    ADD COLUMN user_id UUID UNIQUE REFERENCES app_user(id) ON DELETE CASCADE;