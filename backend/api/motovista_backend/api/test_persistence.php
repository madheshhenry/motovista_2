<?php
require_once '../config/db_connect.php';
header('Content-Type: application/json');

try {
    // 1. Get a valid customer ID
    $row = $conn->query("SELECT id FROM customers LIMIT 1")->fetch(PDO::FETCH_ASSOC);
    if (!$row) {
        throw new Exception("No customers found in DB!");
    }
    $custId = $row['id'];

    // 2. Attempt to insert a test notification
    require_once '../includes/FCMManager.php';
    $result = FCMManager::sendNotification($custId, "Persistence Test", "If you see this, DB persistence is working!", ["type" => "test"]);

    // 3. Check if it's in the table
    $stmt = $conn->prepare("SELECT * FROM customer_notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 1");
    $stmt->execute([$custId]);
    $persistenceCheck = $stmt->fetch(PDO::FETCH_ASSOC);

    echo json_encode([
        "success" => true,
        "customer_id_tested" => $custId,
        "fcm_result" => $result,
        "persisted_data" => $persistenceCheck
    ]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>