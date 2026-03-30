<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

// Disable error display to prevent HTML injection in JSON
ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception("Method Not Allowed");
    }

    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!$data || !isset($data['request_id'])) {
        throw new Exception("Missing required field: request_id");
    }

    $requestId = $data['request_id'];
    $status = 'completed'; // Final status

    // Check previous status to prevent double inventory deduction
    $checkSql = "SELECT status FROM customer_requests WHERE id = :id";
    $checkStmt = $conn->prepare($checkSql);
    $checkStmt->execute([':id' => $requestId]);
    $currentStatus = $checkStmt->fetchColumn();

    // If already approved/accepted, inventory was likely already deducted. Skip to prevent double count.
    $inventoryAlreadyDeducted = in_array(strtolower($currentStatus), ['approved', 'accepted', 'completed', 'delivered']);

    $sql = "UPDATE customer_requests SET status = :status WHERE id = :id";
    $stmt = $conn->prepare($sql);
    if ($stmt->execute([':status' => $status, ':id' => $requestId])) {

        // --- INVENTORY UPDATE LOGIC (Start) ---
        if (!$inventoryAlreadyDeducted) {

            // --- INVENTORY UPDATE LOGIC (Start) ---
            // 1. Get Request Details
            $reqSql = "SELECT customer_name, bike_name, bike_variant, bike_color FROM customer_requests WHERE id = :id";
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

                // 2. Find ONE available bike (FIFO)
                $findBikeSql = "SELECT id FROM bikes 
                               WHERE model LIKE :model 
                               AND variant LIKE :variant 
                               AND colors LIKE :color 
                               AND condition_type = 'NEW'
                               AND (status IS NULL OR status = 'Available')
                               ORDER BY date ASC 
                               LIMIT 1";

                $stmtFind = $conn->prepare($findBikeSql);
                $stmtFind->execute([
                    ':model' => '%' . $model . '%',
                    ':variant' => '%' . $variant . '%',
                    ':color' => '%' . $color . '%'
                ]);
                $bike = $stmtFind->fetch(PDO::FETCH_ASSOC);

                if ($bike) {
                    $assignedBikeId = $bike['id'];

                    // 3. Mark as Sold
                    $updateBikeSql = "UPDATE bikes 
                                   SET status = 'Sold', 
                                       sold_date = NOW(), 
                                       customer_name = :custName,
                                       left_inventory_date = NOW()
                                   WHERE id = :bikeId";

                    $stmtUpdate = $conn->prepare($updateBikeSql);
                    $stmtUpdate->execute([
                        ':custName' => $reqData['customer_name'],
                        ':bikeId' => $assignedBikeId
                    ]);
                }
            }
        }

        // --- REGISTRATION LEDGER CREATION (Start) ---
        // Create a new entry in registration_ledger
        $ledgerSql = "INSERT INTO registration_ledger (order_id, customer_id, customer_name, bike_name, physical_bike_id, step_1_status, step_2_status, step_3_status, step_4_status) 
                          VALUES (:orderId, :custId, :custName, :bikeName, :bikeId, 'pending', 'locked', 'locked', 'locked')";
        $ledgerStmt = $conn->prepare($ledgerSql);

        // Re-fetch details if needed, or use existing reqData
        $infoSql = "SELECT customer_id, customer_name, bike_name FROM customer_requests WHERE id = :id";
        $infoStmt = $conn->prepare($infoSql);
        $infoStmt->execute([':id' => $requestId]);
        $infoData = $infoStmt->fetch(PDO::FETCH_ASSOC);

        if ($infoData) {
            $ledgerStmt->execute([
                ':orderId' => $requestId,
                ':custId' => $infoData['customer_id'],
                ':custName' => $infoData['customer_name'],
                ':bikeName' => $infoData['bike_name'],
                ':bikeId' => isset($assignedBikeId) ? $assignedBikeId : null
            ]);
        }
        // --- REGISTRATION LEDGER CREATION (End) ---

        // --- INVENTORY UPDATE LOGIC (End) ---

        echo json_encode(["success" => true, "message" => "Order completed successfully"]);
    } else {
        throw new Exception("Database Update Failed");
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>