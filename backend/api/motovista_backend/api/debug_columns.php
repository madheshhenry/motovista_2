<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
try {
    $stmt = $conn->query("SHOW COLUMNS FROM registration_ledger");
    $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode($columns);
} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>
