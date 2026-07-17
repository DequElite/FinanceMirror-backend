CREATE TABLE password_recovery (
                                   id UUID PRIMARY KEY,
                                   token VARCHAR(255) NOT NULL UNIQUE,
                                   expires_at TIMESTAMP NOT NULL,
                                   user_id UUID UNIQUE,

                                   CONSTRAINT fk_password_recovery_user
                                       FOREIGN KEY (user_id)
                                           REFERENCES app_user(id)
                                           ON DELETE CASCADE
);