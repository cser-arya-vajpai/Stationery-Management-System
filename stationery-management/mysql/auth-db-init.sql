CREATE DATABASE IF NOT EXISTS auth_db;
USE auth_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Inserts default admin with password 'admin123' (BCrypt hashed)
INSERT INTO users (name, email, password, role) 
SELECT 'System Admin', 'admin@stationery.com', '$2a$10$8.225IP3OI5RyMRvj1uqMeJRr4s66E1e.x.4bQ/R3X3B8yW6Fh6d2', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@stationery.com');