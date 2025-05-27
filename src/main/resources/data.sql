-- Create an Admin User
MERGE INTO users (first_name, last_name, username, password, role)
    KEY(username)
    VALUES ('Admin', 'User', 'admin', '$2a$12$/kDqTRezNu6ZvYVvFR/B2OowNJNzCWSXS6bkwo/UEpxt0m1XJJOcS', 'ADMIN');

-- Create a User
MERGE INTO users (first_name, last_name, username, password, role)
    KEY(username)
    VALUES ('Normal', 'User', 'user', '$2a$12$xX3uovptRohYYhgAOpRAjuQL2oNAWpD.gEISA/JnDbgBkcZTwXECi', 'USER');