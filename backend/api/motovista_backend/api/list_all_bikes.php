<?php
require_once '../config/db_connect.php';
$bikes = $conn->query("SELECT id, brand, model, variant, colors, condition_type, status FROM bikes")->fetchAll(PDO::FETCH_ASSOC);
print_r($bikes);
?>