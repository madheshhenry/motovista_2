<?php
require_once '../config/db_connect.php';
$stmt = $conn->query("SELECT id, brand, model, engine_number FROM bikes WHERE id BETWEEN 340 AND 360");
print_r($stmt->fetchAll(PDO::FETCH_ASSOC));
?>