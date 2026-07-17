CREATE TABLE subscription (
                              id UUID PRIMARY KEY,

                              type VARCHAR(50),
                              status VARCHAR(50),

                              created_at DATE,
                              started_at DATE NOT NULL,
                              updated_at DATE NOT NULL,
                              expires_at DATE NOT NULL
);

CREATE TABLE billing (
                         id UUID PRIMARY KEY,

                         user_id UUID NOT NULL,
                         subscription_id UUID NOT NULL,

                         paypal_order_id VARCHAR(255) NOT NULL,
                         paypal_capture_id VARCHAR(255) NOT NULL,

                         paypal_response JSONB NOT NULL,

                         amount NUMERIC(19,2) NOT NULL,

                         currency VARCHAR(3) NOT NULL DEFAULT 'UAH',

                         status VARCHAR(50),

                         created_at DATE,

                         CONSTRAINT fk_billing_user
                             FOREIGN KEY (user_id)
                                 REFERENCES app_user(id)
                                 ON DELETE CASCADE,

                         CONSTRAINT fk_billing_subscription
                             FOREIGN KEY (subscription_id)
                                 REFERENCES subscription(id)
                                 ON DELETE CASCADE
);

ALTER TABLE app_user
    ADD COLUMN subscription_id UUID;

ALTER TABLE app_user
    ADD CONSTRAINT fk_app_user_subscription
        FOREIGN KEY (subscription_id)
            REFERENCES subscription(id);

ALTER TABLE app_user
    ADD CONSTRAINT uk_app_user_subscription
        UNIQUE (subscription_id);