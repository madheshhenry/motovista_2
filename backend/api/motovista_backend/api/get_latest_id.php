<?php
require_once '../config/db_connect.php';
$stmt = $conn->query("SELECT id FROM customer_requests WHERE status='pending' ORDER BY id DESC LIMIT 1");
$row = $stmt->fetch(PDO::FETCH_ASSOC);
echo $row ? $row['id'] : '1';
?>