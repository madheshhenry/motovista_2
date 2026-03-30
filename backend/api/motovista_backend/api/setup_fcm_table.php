<?php
require_once '../config/db_connect.php';

try {
    $sql = "CREATE TABLE IF NOT EXISTS user_fcm_tokens (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        user_type ENUM('customer', 'admin') DEFAULT 'customer',
        fcm_token TEXT NOT NULL,
        device_name VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        UNIQUE(user_id, user_type, fcm_token(255))
    )";

    $conn->exec($sql);
    echo json_encode(["success" => true, "message" => "Table 'user_fcm_tokens' created successfully"]);

} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Error creating table: " . $e->getMessage()]);
}
?>