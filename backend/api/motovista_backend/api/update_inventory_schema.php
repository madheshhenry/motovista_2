<?php
require_once '../config/db_connect.php';

// Helper function to check and add column
function addColumnIfNotExists($conn, $table, $column, $definition)
{
    try {
        $check = $conn->query("SHOW COLUMNS FROM $table LIKE '$column'");
        if ($check->rowCount() == 0) {
            $sql = "ALTER TABLE $table ADD COLUMN $column $definition";
            $conn->exec($sql);
            echo "Added column $column to $table.<br>";
        } else {
            echo "Column $column already exists in $table.<br>";
        }
    } catch (PDOException $e) {
        echo "Error adding $column: " . $e->getMessage() . "<br>";
    }
}

try {
    echo "Starting schema update...<br>";

    // Add columns
    addColumnIfNotExists($conn, 'bikes', 'status', "VARCHAR(50) DEFAULT 'Available'");
    addColumnIfNotExists($conn, 'bikes', 'sold_date', "DATETIME NULL");
    addColumnIfNotExists($conn, 'bikes', 'customer_name', "VARCHAR(100) NULL");
    addColumnIfNotExists($conn, 'bikes', 'left_inventory_date', "DATETIME NULL");

    echo "Schema update completed.";

} catch (Exception $e) {
    echo "General Error: " . $e->getMessage();
}
?>