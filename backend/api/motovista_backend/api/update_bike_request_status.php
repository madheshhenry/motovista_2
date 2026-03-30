<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception("Method Not Allowed");
    }

    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!$data || !isset($data['request_id']) || !isset($data['status'])) {
        throw new Exception("Missing required fields");
    }

    $requestId = $data['request_id'];
    $status = $data['status'];

    $sql = "UPDATE bike_requests SET status = :status WHERE id = :id";
    $stmt = $conn->prepare($sql);
    $result = $stmt->execute([
        ':status' => $status,
        ':id' => $requestId
    ]);

    if ($result) {
        // Send Notification to Customer
        try {
            require_once '../includes/FCMManager.php';
            $custSql = "SELECT user_id, bike_model FROM bike_requests WHERE id = :id";
            $custStmt = $conn->prepare($custSql);
            $custStmt->execute([':id' => $requestId]);
            $custData = $custStmt->fetch(PDO::FETCH_ASSOC);

            if ($custData) {
                $statusUpper = strtoupper($status);
                $title = "Bike Request " . ($statusUpper === 'APPROVED' ? 'Accepted' : $statusUpper);
                $message = "Your special request for " . $custData['bike_model'] . " has been " . $status . ".";
                FCMManager::sendNotification(
                    $custData['user_id'],
                    $title,
                    $message,
                    [
                        "type" => "bike_request_update",
                        "id" => $requestId,
                        "screen" => "OrderStatusActivity", // We redirect to unified order status if applicable
                        "request_id" => $requestId
                    ]
                );
            }
        } catch (Exception $e) {
            error_log("FCM Error in bike request update: " . $e->getMessage());
        }

        echo json_encode(["success" => true, "message" => "Status updated successfully"]);
    } else {
        throw new Exception("Update failed");
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>