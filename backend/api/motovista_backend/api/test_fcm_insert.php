<?php
require_once __DIR__ . '/../config/db_connect.php';

$user_id = 999;
$fcm_token = "dummy_token_" . time();
$user_type = "admin";
$device_name = "CLI Tester";

try {
    $stmt = $conn->prepare("INSERT INTO user_fcm_tokens (user_id, fcm_token, device_name, user_type) 
                           VALUES (?, ?, ?, ?) 
                           ON DUPLICATE KEY UPDATE device_name = ?, updated_at = CURRENT_TIMESTAMP");

    $stmt->execute([$user_id, $fcm_token, $device_name, $user_type, $device_name]);

    echo "Successfully inserted/updated dummy token for $user_type (ID: $user_id)\n";

} catch (PDOException $e) {
    echo "Database error: " . $e->getMessage() . "\n";
}
?>