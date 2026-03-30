<?php
require_once '../config/db_connect.php';
$stmt = $conn->query("SHOW COLUMNS FROM bikes");
$columns = $stmt->fetchAll(PDO::FETCH_COLUMN);

echo "Colors: " . (in_array('colors', $columns) ? "YES" : "NO") . "\n";
echo "Color: " . (in_array('color', $columns) ? "YES" : "NO") . "\n";
echo "Condition Type: " . (in_array('condition_type', $columns) ? "YES" : "NO") . "\n";
echo "Status: " . (in_array('status', $columns) ? "YES" : "NO") . "\n";
?>