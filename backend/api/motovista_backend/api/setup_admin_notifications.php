<?php
require_once '../config/db_connect.php';

try {
    $sql = "CREATE TABLE IF NOT EXISTS admin_notifications (
        id INT AUTO_INCREMENT PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        message TEXT NOT NULL,
        type VARCHAR(50) DEFAULT 'general',
        is_read TINYINT(1) DEFAULT 0,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )";

    $conn->exec($sql);
    echo json_encode(["success" => true, "message" => "Table 'admin_notifications' created successfully"]);

} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Error creating table: " . $e->getMessage()]);
}
?>