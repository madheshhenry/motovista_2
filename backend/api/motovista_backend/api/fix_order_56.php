<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // 1. Find the latest order for "max" (ID 56)
    $stmt = $conn->prepare("SELECT * FROM customer_requests WHERE id = 56");
    $stmt->execute();
    $order = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$order) {
        throw new Exception("Order ID 56 not found");
    }

    // 2. Find an available Yamaha R13 or similar
    $bikeStmt = $conn->prepare("SELECT id FROM bikes WHERE (model LIKE '%Yamaha%' OR model LIKE '%R13%') AND status = 'Available' LIMIT 1");
    $bikeStmt->execute();
    $bike = $bikeStmt->fetch(PDO::FETCH_ASSOC);

    if (!$bike) {
         // Fallback to any bike if Yamaha not found
         $bikeStmt = $conn->prepare("SELECT id FROM bikes WHERE status = 'Available' LIMIT 1");
         $bikeStmt->execute();
         $bike = $bikeStmt->fetch(PDO::FETCH_ASSOC);
    }

    $bikeId = $bike ? $bike['id'] : null;

    if ($bikeId) {
        $updateBike = $conn->prepare("UPDATE bikes SET status = 'Sold', customer_name = 'max' WHERE id = :id");
        $updateBike->execute([':id' => $bikeId]);
    }

    // 3. Create a Registration Ledger entry for ID 56
    $ins = $conn->prepare("INSERT INTO registration_ledger (order_id, customer_id, customer_name, bike_name, physical_bike_id, step_1_status) 
                          VALUES (56, :cid, 'max', :bikeName, :bid, 'pending')");
    $ins->execute([
        ':cid' => $order['customer_id'],
        ':bikeName' => $order['bike_name'],
        ':bid' => $bikeId
    ]);

    echo json_encode(["success" => true, "message" => "Restored Order 56 into the ledger and linked to bike ID $bikeId"]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>
