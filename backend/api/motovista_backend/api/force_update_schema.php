<?php
require_once '../config/db_connect.php';

try {
    echo "Adding status column...\n";
    $conn->exec("ALTER TABLE bikes ADD COLUMN status VARCHAR(50) DEFAULT 'Available'");
    echo "Success.\n";
} catch (PDOException $e) {
    echo "Status: " . $e->getMessage() . "\n";
}

try {
    echo "Adding sold_date column...\n";
    $conn->exec("ALTER TABLE bikes ADD COLUMN sold_date DATETIME NULL");
    echo "Success.\n";
} catch (PDOException $e) {
    echo "Sold Date: " . $e->getMessage() . "\n";
}

try {
    echo "Adding customer_name column...\n";
    $conn->exec("ALTER TABLE bikes ADD COLUMN customer_name VARCHAR(100) NULL");
    echo "Success.\n";
} catch (PDOException $e) {
    echo "Customer Name: " . $e->getMessage() . "\n";
}

try {
    echo "Adding left_inventory_date column...\n";
    $conn->exec("ALTER TABLE bikes ADD COLUMN left_inventory_date DATETIME NULL");
    echo "Success.\n";
} catch (PDOException $e) {
    echo "Left Inventory: " . $e->getMessage() . "\n";
}

echo "Done.";
?>