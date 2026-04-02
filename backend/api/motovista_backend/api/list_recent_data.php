<?php
header('Content-Type: text/plain');
require_once '../config/db_connect.php';

try {
    echo "=== RECENT CUSTOMER REQUESTS ===\n";
    $stmt = $conn->query("SELECT id, customer_name, bike_name, status FROM customer_requests ORDER BY id DESC LIMIT 20");
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
    foreach ($rows as $row) {
        echo "ID: " . $row['id'] . ", Name: " . $row['customer_name'] . ", Bike: " . $row['bike_name'] . ", Status: " . $row['status'] . "\n";
    }

    echo "\n=== RECENT REGISTRATION LEDGER ===\n";
    $stmtL = $conn->query("SELECT id, order_id, customer_name, bike_name, physical_bike_id FROM registration_ledger ORDER BY id DESC LIMIT 20");
    $rowsL = $stmtL->fetchAll(PDO::FETCH_ASSOC);
    foreach ($rowsL as $rowL) {
        echo "ID: " . $rowL['id'] . ", OrderID: " . $rowL['order_id'] . ", Name: " . $rowL['customer_name'] . ", BikeID: " . ($rowL['physical_bike_id'] ?? 'NULL') . "\n";
    }

} catch (PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>
