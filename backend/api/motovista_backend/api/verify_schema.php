<?php
require_once '../config/db_connect.php';
$stmt = $conn->query("SHOW COLUMNS FROM bikes");
$columns = $stmt->fetchAll(PDO::FETCH_COLUMN);
echo "Columns: " . implode(", ", $columns);
?>