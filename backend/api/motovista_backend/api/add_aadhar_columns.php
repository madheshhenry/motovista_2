<?php
require_once '../config/db_connect.php';

try {
    $sql = "ALTER TABLE customers ADD COLUMN IF NOT EXISTS aadhar_front VARCHAR(255) DEFAULT NULL";
    $conn->exec($sql);
    echo "Added aadhar_front column if not exists.<br>";

    $sql = "ALTER TABLE customers ADD COLUMN IF NOT EXISTS aadhar_back VARCHAR(255) DEFAULT NULL";
    $conn->exec($sql);
    echo "Added aadhar_back column if not exists.<br>";

} catch (PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>