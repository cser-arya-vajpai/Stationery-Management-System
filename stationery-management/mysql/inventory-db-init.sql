CREATE DATABASE IF NOT EXISTS inventory_db;
USE inventory_db;

CREATE TABLE IF NOT EXISTS stationery_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    available_quantity INT NOT NULL,
    minimum_quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_updated_by VARCHAR(255)
);

-- Seed baseline items
INSERT INTO stationery_items (name, category, unit, available_quantity, minimum_quantity, last_updated_by)
SELECT 'A4 Printing Paper', 'PAPER', 'Ream', 150, 20, 'admin@stationery.com'
WHERE NOT EXISTS (SELECT 1 FROM stationery_items WHERE name = 'A4 Printing Paper');

INSERT INTO stationery_items (name, category, unit, available_quantity, minimum_quantity, last_updated_by)
SELECT 'Blue Ballpoint Pen', 'PEN', 'Box', 80, 15, 'admin@stationery.com'
WHERE NOT EXISTS (SELECT 1 FROM stationery_items WHERE name = 'Blue Ballpoint Pen');

INSERT INTO stationery_items (name, category, unit, available_quantity, minimum_quantity, last_updated_by)
SELECT 'HB Pencil', 'PENCIL', 'Box', 120, 30, 'admin@stationery.com'
WHERE NOT EXISTS (SELECT 1 FROM stationery_items WHERE name = 'HB Pencil');

INSERT INTO stationery_items (name, category, unit, available_quantity, minimum_quantity, last_updated_by)
SELECT 'Ruled Notebook 200pg', 'NOTEBOOK', 'Piece', 5, 20, 'admin@stationery.com'
WHERE NOT EXISTS (SELECT 1 FROM stationery_items WHERE name = 'Ruled Notebook 200pg');

INSERT INTO stationery_items (name, category, unit, available_quantity, minimum_quantity, last_updated_by)
SELECT 'Dustless Eraser', 'ERASER', 'Box', 50, 10, 'admin@stationery.com'
WHERE NOT EXISTS (SELECT 1 FROM stationery_items WHERE name = 'Dustless Eraser');

INSERT INTO stationery_items (name, category, unit, available_quantity, minimum_quantity, last_updated_by)
SELECT 'Heavy Duty Stapler', 'STAPLER', 'Piece', 0, 5, 'admin@stationery.com'
WHERE NOT EXISTS (SELECT 1 FROM stationery_items WHERE name = 'Heavy Duty Stapler');