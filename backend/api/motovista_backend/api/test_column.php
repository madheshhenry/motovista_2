<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $stmt = $conn->query("SELECT user_type FROM user_fcm_tokens LIMIT 1");
    echo json_encode(["success" => true, "message" => "Column user_type access successful"]);
} catch (Exception $e) {
    echo json_encode(["success" => false, "error" => $e->getMessage()]);
}
?>