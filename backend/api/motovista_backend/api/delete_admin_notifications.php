<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

try {
    // 1. Verify Admin Token
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';
    if (!$authHeader) throw new Exception("Authorization token missing");
    
    $token = str_replace('Bearer ', '', $authHeader);
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);
    if (!$payload) throw new Exception("Unauthorized access");

    // 2. Get Request Data
    $data = json_decode(file_get_contents('php://input'), true);
    if (!isset($data['items']) || !is_array($data['items'])) {
        throw new Exception("Invalid request data");
    }

    $deletedCount = 0;
    $conn->beginTransaction();

    foreach ($data['items'] as $item) {
        $id = $item['id'];
        $type = $item['type'];

        if ($type === 'emi') {
            // For EMI notifications, we DON'T delete the payment record. 
            // We just skip it or you can add a 'is_dismissed' flag to emi_payments.
            // For now, let's just skip it as it's a task.
            continue; 
        }

        // Persistent Notifications (system, order, info, etc.)
        $stmt = $conn->prepare("DELETE FROM admin_notifications WHERE id = ?");
        $stmt->execute([$id]);
        $deletedCount += $stmt->rowCount();
    }

    $conn->commit();

    echo json_encode([
        "success" => true,
        "message" => "$deletedCount notifications deleted successfully",
        "deleted_count" => $deletedCount
    ]);

} catch (Exception $e) {
    if ($conn->inTransaction()) $conn->rollBack();
    echo json_encode([
        "success" => false, 
        "message" => "Error deleting notifications: " . $e->getMessage()
    ]);
}
?>
