<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
try {
    $stmt = $conn->query("SHOW CREATE TABLE registration_ledger");
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    echo json_encode($row);
} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>