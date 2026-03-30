<?php
require_once __DIR__ . '/../config/db_connect.php';

$stmt = $conn->query("SELECT id, model FROM bikes");
$bikes = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo "Available Bikes:\n";
print_r($bikes);
?>