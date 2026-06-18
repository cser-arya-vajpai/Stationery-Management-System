CREATE DATABASE IF NOT EXISTS request_db;
USE request_db;

CREATE TABLE IF NOT EXISTS stationery_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(100) NOT NULL,
    student_email VARCHAR(255) NOT NULL,
    item_id BIGINT NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    requested_quantity INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    rejection_reason VARCHAR(255),
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);