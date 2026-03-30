<?php
// create_brands_table.php

require_once '../config/db_connect.php';

try {
    $sql = "CREATE TABLE IF NOT EXISTS brands (
        id INT AUTO_INCREMENT PRIMARY KEY,
        brand_name VARCHAR(100) NOT NULL UNIQUE,
        brand_logo VARCHAR(255) DEFAULT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )";

    $conn->exec($sql);
    echo "Table 'brands' created successfully or already exists.";

} catch (PDOException $e) {
    echo "Error creating table: " . $e->getMessage();
}
?>