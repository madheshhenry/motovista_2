<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $stmt = $conn->query("SHOW CREATE TABLE user_fcm_tokens");
    echo json_encode($stmt->fetch(PDO::FETCH_ASSOC), JSON_PRETTY_PRINT);
} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>