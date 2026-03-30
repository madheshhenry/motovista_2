<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // 1. Add user_type column if not exists
    $stmt = $conn->query("DESCRIBE user_fcm_tokens");
    $columns = $stmt->fetchAll(PDO::FETCH_COLUMN);

    if (!in_array('user_type', $columns)) {
        $conn->exec("ALTER TABLE user_fcm_tokens ADD COLUMN user_type ENUM('customer', 'admin') DEFAULT 'customer' AFTER user_id");
    }

    // 2. Add new unique index if not exists
    $stmt = $conn->query("SHOW INDEX FROM user_fcm_tokens WHERE Key_name = 'idx_user_token_v2'");
    if (!$stmt->fetch()) {
        $conn->exec("ALTER TABLE user_fcm_tokens ADD UNIQUE INDEX idx_user_token_v2 (user_id, user_type, fcm_token(255))");
    }

    echo json_encode(["success" => true, "message" => "Table 'user_fcm_tokens' updated successfully"]);

} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Error updating table: " . $e->getMessage()]);
}
?>