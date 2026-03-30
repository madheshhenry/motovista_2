<?php
require_once '../config/db_connect.php';

$sql = "SELECT id, brand, model, engine_number, chassis_number, created_at FROM stock_bikes WHERE model LIKE '%R16 V2%'";
$stmt = $conn->prepare($sql);
$stmt->execute();
$results = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo "Stock Bikes (R16 V2):\n";
print_r($results);

$sql2 = "SELECT id, brand, model, engine_number, chassis_number FROM bikes WHERE model LIKE '%R16 V2%'";
$stmt2 = $conn->prepare($sql2);
$stmt2->execute();
$results2 = $stmt2->fetchAll(PDO::FETCH_ASSOC);
echo "Bikes (R16 V2):\n";
print_r($results2);
?>