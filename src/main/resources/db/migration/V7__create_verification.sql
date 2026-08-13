CREATE TABLE verification (
                              id UUID PRIMARY KEY,
                              token VARCHAR(255) NOT NULL UNIQUE,
                              code VARCHAR(255) NOT NULL,
                              expires_at TIMESTAMP NOT NULL,
                              attempts INTEGER DEFAULT 0
);

ALTER TABLE app_user
    ADD COLUMN verification_id UUID;

ALTER TABLE app_user
    ADD CONSTRAINT uk_app_user_verification
        UNIQUE (verification_id);

ALTER TABLE app_user
    ADD CONSTRAINT fk_app_user_verification
        FOREIGN KEY (verification_id)
            REFERENCES verification(id)
            ON DELETE SET NULL;