ALTER TABLE users ADD COLUMN IF NOT EXISTS password_hash text;
CREATE UNIQUE INDEX IF NOT EXISTS trustnet_users_email ON users (email);
ALTER TABLE scan_history ADD COLUMN IF NOT EXISTS user_id varchar REFERENCES users(id);
CREATE INDEX IF NOT EXISTS trustnet_scan_owner ON scan_history (user_id, created_at DESC);
CREATE TABLE IF NOT EXISTS trustnet_sessions (
  token_hash text PRIMARY KEY,
  user_id varchar NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  expires_at timestamptz NOT NULL
);
