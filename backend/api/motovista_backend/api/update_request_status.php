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
            // 1. Get Request Details
            $reqSql = "SELECT customer_id, customer_name, bike_name, bike_variant, bike_color FROM customer_requests WHERE id = :id";
            $reqStmt = $conn->prepare($reqSql);
            $reqStmt->execute([':id' => $requestId]);
            $reqData = $reqStmt->fetch(PDO::FETCH_ASSOC);

            if ($reqData) {
                $searchModel = trim($reqData['bike_name']);
                $searchColor = trim($reqData['bike_color']);
                if (strpos($searchColor, '|') !== false) {
                    $searchColor = trim(explode('|', $searchColor)[0]);
                }

                // BULLETPROOF FIFO SEARCH (First bike added by brand or model match)
                $findBikeSql = "SELECT id FROM bikes 
                               WHERE status = 'Available' 
                               AND condition_type = 'NEW'
                               AND (
                                   (LOWER(REPLACE(model, ' ', '')) = LOWER(REPLACE(:model, ' ', '')))
                                   OR (model LIKE :model_like)
                                   OR (CONCAT(LOWER(brand), ' ', LOWER(model)) = LOWER(:full_name))
                               )
                               AND (colors LIKE :color OR LOWER(colors) LIKE :color_exact)
                               ORDER BY id ASC 
                               LIMIT 1";

                $stmtFind = $conn->prepare($findBikeSql);
                $stmtFind->execute([
                    ':model' => $searchModel,
                    ':model_like' => '%' . str_replace(' ', '%', $searchModel) . '%',
                    ':full_name' => $searchModel,
                    ':color' => '%' . $searchColor . '%',
                    ':color_exact' => '%' . strtolower($searchColor) . '%'
                ]);
                $bike = $stmtFind->fetch(PDO::FETCH_ASSOC);

                if ($bike) {
                    $assignedBikeId = $bike['id'];

                    // 2. Mark as Sold
                    $updateBikeSql = "UPDATE bikes SET status = 'Sold', sold_date = NOW(), customer_name = :custName, left_inventory_date = NOW() WHERE id = :bid";
                    $stmtUpdate = $conn->prepare($updateBikeSql);
                    $stmtUpdate->execute([':custName' => $reqData['customer_name'], ':bid' => $assignedBikeId]);
                } else if (strtolower($status) === 'completed') {
                    // Fail if completing but no stock found
                    throw new Exception("INVENTORY ERROR: No physical bike matching '$searchModel' in color '$searchColor' is available in your showroom. Please add stock first.");
                }
            }
        }

        // --- REGISTRATION LEDGER CREATION/UPDATE ---
        if (strtolower($status) === 'approved' || strtolower($status) === 'accepted' || strtolower($status) === 'completed') {
            
            // Re-fetch if needed
            if (!isset($reqData)) {
                $reqStmt = $conn->prepare("SELECT customer_id, customer_name, bike_name FROM customer_requests WHERE id = :id");
                $reqStmt->execute([':id' => $requestId]);
                $reqData = $reqStmt->fetch(PDO::FETCH_ASSOC);
            }

            if ($reqData) {
                $checkLedger = $conn->prepare("SELECT id FROM registration_ledger WHERE order_id = :oid");
                $checkLedger->execute([':oid' => $requestId]);
                $existing = $checkLedger->fetch(PDO::FETCH_ASSOC);

                if ($existing) {
                    $upL = $conn->prepare("UPDATE registration_ledger SET physical_bike_id = :bid WHERE order_id = :oid");
                    $upL->execute([':bid' => isset($assignedBikeId) ? $assignedBikeId : null, ':oid' => $requestId]);
                } else {
                    $insL = $conn->prepare("INSERT INTO registration_ledger (order_id, customer_id, customer_name, bike_name, physical_bike_id, step_1_status, step_2_status, step_3_status, step_4_status) 
                                           VALUES (:oid, :cid, :cn, :bn, :bid, 'pending', 'locked', 'locked', 'locked')");
                    $insL->execute([
                        ':oid' => $requestId,
                        ':cid' => $reqData['customer_id'],
                        ':cn' => $reqData['customer_name'],
                        ':bn' => $reqData['bike_name'],
                        ':bid' => isset($assignedBikeId) ? $assignedBikeId : null
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