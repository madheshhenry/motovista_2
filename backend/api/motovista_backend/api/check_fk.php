<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $stmt = $conn->query("SELECT TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME 
                          FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
                          WHERE TABLE_NAME = 'user_fcm_tokens' AND TABLE_SCHEMA = DATABASE() AND REFERENCED_TABLE_NAME IS NOT NULL");
    echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC), JSON_PRETTY_PRINT);
} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>