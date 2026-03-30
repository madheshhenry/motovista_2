<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

// Disable error display
ini_set('display_errors', 0);
error_reporting(E_ALL);

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
    $status = $data['status']; // 'approved' or 'rejected'

    // Check previous status to prevent double inventory deduction
    $checkSql = "SELECT status FROM customer_requests WHERE id = :id";
    $checkStmt = $conn->prepare($checkSql);
    $checkStmt->execute([':id' => $requestId]);
    $currentStatus = $checkStmt->fetchColumn();

    // If already approved/accepted, inventory was likely already deducted. Skip.
    $inventoryAlreadyDeducted = in_array(strtolower($currentStatus), ['approved', 'accepted', 'completed', 'delivered']);

    $sql = "UPDATE customer_requests SET status = :status WHERE id = :id";
    $stmt = $conn->prepare($sql);

    if ($stmt->execute([':status' => $status, ':id' => $requestId])) {

        // Inventory Update Logic
        if ((strtolower($status) === 'approved' || strtolower($status) === 'accepted' || strtolower($status) === 'completed') && !$inventoryAlreadyDeducted) {
            // 1. Get Request Details (Bike Model, Variant, Color, Customer Name)
            $reqSql = "SELECT customer_name, bike_name, bike_variant, bike_color, customer_id FROM customer_requests WHERE id = :id";
            $reqStmt = $conn->prepare($reqSql);
            $reqStmt->execute([':id' => $requestId]);
            $reqData = $reqStmt->fetch(PDO::FETCH_ASSOC);

            if ($reqData) {
                $model = trim($reqData['bike_name']);
                $variant = trim($reqData['bike_variant']);

                // Handle Color Parsing (Name|Hex)
                $color = trim($reqData['bike_color']);
                if (strpos($color, '|') !== false) {
                    $parts = explode('|', $color);
                    $color = trim($parts[0]);
                }

                // 2. Find ONE available bike and mark as Sold
                // We order by date ASC to sell the oldest stock first (FIFO)
                $updateBikeSql = "UPDATE bikes 
                                   SET status = 'Sold', 
                                       sold_date = NOW(), 
                                       customer_name = :custName,
                                       left_inventory_date = NOW()
                                   WHERE id = (
                                       SELECT id FROM (
                                           SELECT id FROM bikes 
                                           WHERE model LIKE :model 
                                           AND variant LIKE :variant 
                                           AND colors LIKE :color 
                                           AND condition_type = 'NEW'
                                           AND (status IS NULL OR status = 'Available')
                                           ORDER BY date ASC 
                                           LIMIT 1
                                       ) as tmp
                                   )";

                $stmtUpdate = $conn->prepare($updateBikeSql);
                $stmtUpdate->execute([
                    ':custName' => $reqData['customer_name'],
                    ':model' => $model,
                    ':variant' => $variant,
                    ':color' => '%' . $color . '%'
                ]);
            }
        }

        // --- REGISTRATION LEDGER CREATION (If status is completed) ---
        if (strtolower($status) === 'completed') {
            // Check if ledger already exists to avoid duplicates
            $checkLedgerSql = "SELECT id FROM registration_ledger WHERE order_id = :orderId";
            $checkLedgerStmt = $conn->prepare($checkLedgerSql);
            $checkLedgerStmt->execute([':orderId' => $requestId]);

            if ($checkLedgerStmt->rowCount() == 0) {
                // Fetch details again if needed (or reuse reqData if available, better to be safe)
                $infoSql = "SELECT customer_id, customer_name, bike_name FROM customer_requests WHERE id = :id";
                $infoStmt = $conn->prepare($infoSql);
                $infoStmt->execute([':id' => $requestId]);
                $infoData = $infoStmt->fetch(PDO::FETCH_ASSOC);

                if ($infoData) {
                    $ledgerSql = "INSERT INTO registration_ledger (order_id, customer_id, customer_name, bike_name, step_1_status, step_2_status, step_3_status, step_4_status) 
                                   VALUES (:orderId, :custId, :custName, :bikeName, 'pending', 'locked', 'locked', 'locked')";
                    $ledgerStmt = $conn->prepare($ledgerSql);
                    $ledgerStmt->execute([
                        ':orderId' => $requestId,
                        ':custId' => $infoData['customer_id'],
                        ':custName' => $infoData['customer_name'],
                        ':bikeName' => $infoData['bike_name']
                    ]);
                }
            }
        }

        // Send FCM Notification to User
        try {
            error_log("Attempting to send notification for Request ID: $requestId");
            require_once '../includes/FCMManager.php';
            $custNotifySql = "SELECT customer_id, bike_name FROM customer_requests WHERE id = :id";
            $custNotifyStmt = $conn->prepare($custNotifySql);
            $custNotifyStmt->execute([':id' => $requestId]);
            $custNotifyData = $custNotifyStmt->fetch(PDO::FETCH_ASSOC);

            if ($custNotifyData) {
                error_log("Sending notification to Customer ID: " . $custNotifyData['customer_id']);
                $statusUpper = strtoupper($status);
                $title = "Request " . ($statusUpper === 'APPROVED' ? 'Accepted' : $statusUpper);
                $message = "Congratulations! Your application for " . $custNotifyData['bike_name'] . " has been " . $status . ".";
                if ($statusUpper === 'REJECTED') {
                    $message = "We regret to inform you that your application for " . $custNotifyData['bike_name'] . " was not approved at this time.";
                }
                $notifResult = FCMManager::sendNotification(
                    $custNotifyData['customer_id'],
                    $title,
                    $message,
                    [
                        "type" => "request_update",
                        "id" => $requestId,
                        "screen" => "OrderStatusActivity",
                        "order_id" => $requestId
                    ]
                );
                error_log("FCM Notification Result: " . json_encode($notifResult));
            } else {
                error_log("Customer data not found for Request ID: $requestId");
            }
        } catch (Exception $e) {
            // Log error but don't fail the request
            error_log("FCM Error in update_request_status: " . $e->getMessage());
        }

        echo json_encode(["success" => true, "message" => "Status updated successfully"]);
    } else {
        throw new Exception("Database Update Failed");
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>