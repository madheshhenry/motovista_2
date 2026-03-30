<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $sql = "ALTER TABLE customer_requests ADD COLUMN selected_fittings TEXT AFTER bike_price";
    $conn->exec($sql);
    echo json_encode(["status" => "success", "message" => "Column selected_fittings added successfully"]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>