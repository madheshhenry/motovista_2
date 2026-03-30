<?php
require_once '../config/db_connect.php';

try {
    $sql = "INSERT INTO bikes (brand, model, variant, colors, condition_type, status, date) 
            VALUES ('Yamaha', 'R16 V2', '350', 'orange', 'NEW', 'Available', NOW())";

    $conn->exec($sql);
    echo "Seeded 1 bike successfully.\n";
    echo "ID: " . $conn->lastInsertId();

} catch (PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>