<?php
echo "Starting FCM Verification...\n";
require_once __DIR__ . '/../includes/FCMManager.php';
require_once '../config/db_connect.php';

// Test notification for admin
$title = "Test Push Notification";
$message = "This is a test notification from the server after credential update.";
$type = "test_type";
$id = 1;

echo "Sending test notification...\n";
try {
    // Debug token generation
    $rc = new ReflectionClass('FCMManager');
    $method = $rc->getMethod('getAccessToken');
    $method->setAccessible(true);

    echo "Attempting to generate access token...\n";
    $token = $method->invoke(null);

    if ($token) {
        echo "Access Token generated successfully!\n";
    } else {
        echo "Failed to generate access token.\n";

        $dataMethod = $rc->getMethod('getServiceAccountData');
        $dataMethod->setAccessible(true);
        $data = $dataMethod->invoke(null);
        echo "Private key found: " . (isset($data['private_key']) ? "Yes" : "No") . "\n";
        if (isset($data['private_key'])) {
            $key = $data['private_key'];
            echo "Private key length: " . strlen($key) . "\n";
            echo "First 50 chars of key: " . substr($key, 0, 50) . "...\n";
            echo "Contains literal '\\n': " . (strpos($key, '\n') !== false ? "Yes" : "No") . "\n";
            echo "Contains real newlines: " . (strpos($key, "\n") !== false ? "Yes" : "No") . "\n";
        }
    }

    // Fix: notifyAdmins expects 3 arguments, the 3rd being an array of data
    FCMManager::notifyAdmins($title, $message, [
        'type' => $type,
        'id' => $id,
        'screen' => 'OrderSummaryActivity'
    ]);
    echo "Notification attempt completed. Check server logs.\n";
} catch (Exception $e) {
    echo "Caught Exception: " . $e->getMessage() . "\n";
}
?>