ALTER TABLE session ADD COLUMN user_id UUID;
ALTER TABLE session ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE session
    ADD CONSTRAINT fk_session_user
        FOREIGN KEY (user_id) REFERENCES app_user (id);

ALTER TABLE app_user DROP COLUMN session_id;