<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $stmt = $conn->query("SELECT * FROM user_fcm_tokens LIMIT 1");
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    echo json_encode($row, JSON_PRETTY_PRINT);
} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>