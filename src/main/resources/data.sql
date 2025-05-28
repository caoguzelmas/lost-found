-- Create an Admin User
INSERT INTO users (first_name, last_name, username, password, role)
VALUES ('Admin', 'User', 'admin', '$2a$12$/kDqTRezNu6ZvYVvFR/B2OowNJNzCWSXS6bkwo/UEpxt0m1XJJOcS', 'ADMIN')
    ON CONFLICT (username) DO NOTHING;

-- Create a User
INSERT INTO users (first_name, last_name, username, password, role)
VALUES ('Normal', 'User', 'user', '$2a$12$xX3uovptRohYYhgAOpRAjuQL2oNAWpD.gEISA/JnDbgBkcZTwXECi', 'USER')
    ON CONFLICT (username) DO NOTHING;