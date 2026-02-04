ALTER TABLE users ADD COLUMN is_super_admin BOOLEAN DEFAULT FALSE;
UPDATE users SET is_super_admin = TRUE WHERE username = 'admin'; -- Set default admin as super
