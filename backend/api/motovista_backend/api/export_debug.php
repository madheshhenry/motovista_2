<?php
require_once '../config/db_connect.php';
try {
    $stmt = $conn->query("SELECT * FROM customer_notifications ORDER BY id DESC LIMIT 10");
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);
    file_put_contents('debug_notifications.json', json_encode($data, JSON_PRETTY_PRINT));

    $stmt = $conn->query("SELECT * FROM user_fcm_tokens WHERE user_type = 'customer' ORDER BY updated_at DESC LIMIT 10");
    $tokens = $stmt->fetchAll(PDO::FETCH_ASSOC);
    file_put_contents('debug_tokens.json', json_encode($tokens, JSON_PRETTY_PRINT));

    echo "Files created.\n";
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>