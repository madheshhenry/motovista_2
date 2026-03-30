<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $conn->exec("ALTER TABLE user_fcm_tokens ADD COLUMN user_type ENUM('customer', 'admin') DEFAULT 'customer' AFTER user_id");
    echo json_encode(["success" => true, "message" => "Column added successfully"]);
} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Error: " . $e->getMessage()]);
}
?>