<?php
require_once '../config/db_connect.php';
try {
    echo "--- Customer Notifications (Last 5) ---\n";
    $stmt = $conn->query("SELECT * FROM customer_notifications ORDER BY id DESC LIMIT 5");
    print_r($stmt->fetchAll(PDO::FETCH_ASSOC));

    echo "\n--- Customer FCM Tokens (Last 5) ---\n";
    $stmt = $conn->query("SELECT * FROM user_fcm_tokens WHERE user_type = 'customer' ORDER BY updated_at DESC LIMIT 5");
    print_r($stmt->fetchAll(PDO::FETCH_ASSOC));
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>