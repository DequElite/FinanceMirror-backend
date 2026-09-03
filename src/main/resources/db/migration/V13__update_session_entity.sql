ALTER TABLE session RENAME COLUMN refresh_token_expires_at TO expires_at;

ALTER TABLE session DROP COLUMN latest_login;

ALTER TABLE session ADD COLUMN created_at TIMESTAMP;
UPDATE session SET created_at = now() WHERE created_at IS NULL;
ALTER TABLE session ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE session ALTER COLUMN created_at SET DEFAULT now();

ALTER TABLE session ADD COLUMN ip_address VARCHAR(255);
UPDATE session SET ip_address = '0.0.0.0' WHERE ip_address IS NULL;
ALTER TABLE session ALTER COLUMN ip_address SET NOT NULL;

ALTER TABLE session ADD COLUMN user_agent VARCHAR(255);
UPDATE session SET user_agent = 'unknown' WHERE user_agent IS NULL;
ALTER TABLE session ALTER COLUMN user_agent SET NOT NULL;