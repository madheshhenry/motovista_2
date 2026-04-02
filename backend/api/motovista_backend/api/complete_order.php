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
        // --- FETCH REQUEST DETAILS (Always needed for ledger) ---
        $reqSql = "SELECT customer_id, customer_name, bike_id, bike_name, bike_variant, bike_color FROM customer_requests WHERE id = :id";
        $reqStmt = $conn->prepare($reqSql);
        $reqStmt->execute([':id' => $requestId]);
        $reqData = $reqStmt->fetch(PDO::FETCH_ASSOC);

        if (!$inventoryAlreadyDeducted) {

            if ($reqData) {
                $searchModel = trim($reqData['bike_name'] ?? '');
                $searchVariant = trim($reqData['bike_variant'] ?? '');
                $searchColor = trim($reqData['bike_color'] ?? '');
                
                if (strpos($searchColor, '|') !== false) {
                    $searchColor = trim(explode('|', $searchColor)[0]);
                }

                // STRICT FIFO SEARCH (Picking the first one added by brand or model match)
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
                    ':full_name' => $searchModel, // "Honda CB"
                    ':color' => '%' . $searchColor . '%',
                    ':color_exact' => '%' . strtolower($searchColor) . '%'
                ]);
                $bike = $stmtFind->fetch(PDO::FETCH_ASSOC);

                if ($bike) {
                    $assignedBikeId = $bike['id'];

                    // 4. Mark as Sold (Harden this update)
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
                } else {
                    throw new Exception("INVENTORY ERROR: No physical bike matching '$searchModel' in color '$searchColor' is available.");
                }
            }
        } else if ($reqData) {
            // DEEP SYNC: If already deducted, find the bike that was just sold to this customer
            $searchModel = trim($reqData['bike_name'] ?? '');
            $findSoldSql = "SELECT id FROM bikes 
                            WHERE status = 'Sold' 
                            AND (LOWER(TRIM(customer_name)) = LOWER(TRIM(:custName)))
                            AND (brand LIKE :name OR model LIKE :name OR CONCAT(brand, ' ', model) LIKE :name)
                            ORDER BY id DESC LIMIT 1";
            $stmtSold = $conn->prepare($findSoldSql);
            $stmtSold->execute([':custName' => $reqData['customer_name'], ':name' => '%' . $searchModel . '%']);
            $soldBike = $stmtSold->fetch(PDO::FETCH_ASSOC);
            if ($soldBike) {
                $assignedBikeId = $soldBike['id'];
            }
        }

        // --- REGISTRATION LEDGER CREATION/UPDATE ---
        // Check if ledger exists
        $checkL = $conn->prepare("SELECT id FROM registration_ledger WHERE order_id = :oid");
        $checkL->execute([':oid' => $requestId]);
        $existingLedger = $checkL->fetch(PDO::FETCH_ASSOC);

        if ($existingLedger) {
            // Update existing ledger with physical bike link
            $upLedger = $conn->prepare("UPDATE registration_ledger SET 
                                        physical_bike_id = :bid 
                                        WHERE order_id = :oid");
            $upLedger->execute([
                ':bid' => isset($assignedBikeId) ? $assignedBikeId : null,
                ':oid' => $requestId
            ]);
        } else if ($reqData) {
            // Create new ledger
            $ledgerSql = "INSERT INTO registration_ledger (order_id, customer_id, customer_name, bike_name, physical_bike_id, step_1_status, step_2_status, step_3_status, step_4_status) 
                              VALUES (:orderId, :custId, :custName, :bikeName, :bikeId, 'pending', 'locked', 'locked', 'locked')";
            $ledgerStmt = $conn->prepare($ledgerSql);
            $ledgerStmt->execute([
                ':orderId' => $requestId,
                ':custId' => $reqData['customer_id'],
                ':custName' => $reqData['customer_name'],
                ':bikeName' => $reqData['bike_name'],
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
    http_response_code(400); // Set error status code
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>