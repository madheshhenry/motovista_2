<?php
require_once '../config/db_connect.php';
$stmt = $conn->query("SELECT id, brand, model, engine_number, chassis_number, created_at FROM stock_bikes ORDER BY id DESC LIMIT 20");
print_r($stmt->fetchAll(PDO::FETCH_ASSOC));
?>