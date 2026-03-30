<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

// Get JSON input
$inputJSON = file_get_contents("php://input");
file_put_contents(__DIR__ . '/fcm_request_log.txt', "[" . date('Y-m-d H:i:s') . "] " . $inputJSON . PHP_EOL, FILE_APPEND);
$data = json_decode($inputJSON, true);

if (!$data) {
    error_log("FCM Token Save: Invalid JSON input: " . $inputJSON);
    echo json_encode(["success" => false, "message" => "Invalid JSON input"]);
    exit;
}

if (!isset($data['user_id']) || !isset($data['fcm_token'])) {
    error_log("FCM Token Save: Missing required fields: " . json_encode($data));
    echo json_encode(["success" => false, "message" => "user_id and fcm_token are required"]);
    exit;
}

$user_id = $data['user_id'];
$fcm_token = $data['fcm_token'];
$user_type = isset($data['user_type']) ? $data['user_type'] : 'customer';
$device_name = isset($data['device_name']) ? $data['device_name'] : 'Unknown Device';

try {
    $stmt = $conn->prepare("INSERT INTO user_fcm_tokens (user_id, fcm_token, device_name, user_type) 
                           VALUES (?, ?, ?, ?) 
                           ON DUPLICATE KEY UPDATE device_name = ?, updated_at = CURRENT_TIMESTAMP");

    $stmt->execute([$user_id, $fcm_token, $device_name, $user_type, $device_name]);

    error_log("FCM Token registered for $user_type (ID: $user_id): $fcm_token");

    echo json_encode([
        "success" => true,
        "message" => "Token registered successfully for $user_type"
    ]);

} catch (PDOException $e) {
    error_log("FCM Token Registration Database error: " . $e->getMessage());
    echo json_encode(["success" => false, "message" => "Database error: " . $e->getMessage()]);
}
?>