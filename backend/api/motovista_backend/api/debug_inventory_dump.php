<?php
header('Content-Type: text/plain');
require_once '../config/db_connect.php';

// Fetch raw data
$sql = "SELECT id, model, status, customer_name, sold_date FROM bikes WHERE status = 'Sold' OR status = 'sold'";
$stmt = $conn->prepare($sql);
$stmt->execute();
$soldBikes = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo "=== RAW DB 'SOLD' BIKES ===\n";
print_r($soldBikes);

echo "\n=== API JSON MOCK ===\n";
// Mocking the get_inventory.php logic part
$sqlFull = "SELECT id, brand, model, status, customer_name, sold_date FROM bikes WHERE condition_type = 'NEW' ORDER BY id DESC LIMIT 5";
$stmtFull = $conn->prepare($sqlFull);
$stmtFull->execute();
$allBikes = $stmtFull->fetchAll(PDO::FETCH_ASSOC);

print_r($allBikes);
?>