<?php
require_once '../config/db_connect.php';
echo "Searching stock_bikes:\n";
$stmt = $conn->query("SELECT id, brand, model, variant, engine_number FROM stock_bikes WHERE variant = '350'");
print_r($stmt->fetchAll(PDO::FETCH_ASSOC));

echo "Searching bikes:\n";
$stmt = $conn->query("SELECT id, brand, model, variant, engine_number FROM bikes WHERE variant = '350'");
print_r($stmt->fetchAll(PDO::FETCH_ASSOC));
?>