<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // 1. Find the latest order for "max" (ORD-50)
    $stmt = $conn->prepare("SELECT * FROM customer_requests WHERE customer_name = 'max' AND id = 50");
    $stmt->execute();
    $order = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$order) {
        throw new Exception("Order for 'max' (ID 50) not found");
    }

    // 2. Find an available Honda CB
    $bikeStmt = $conn->prepare("SELECT id FROM bikes WHERE (model LIKE '%CB%' OR model LIKE '%Honda%') AND status = 'Available' LIMIT 1");
    $bikeStmt->execute();
    $bike = $bikeStmt->fetch(PDO::FETCH_ASSOC);

    if (!$bike) {
        throw new Exception("No available Honda/CB bike found in inventory to link");
    }

    $bikeId = $bike['id'];

    // 3. Mark bike as SOLD to max
    $updateBike = $conn->prepare("UPDATE bikes SET status = 'Sold', customer_name = 'max' WHERE id = :id");
    $updateBike->execute([':id' => $bikeId]);

    // 4. Ensure a Registration Ledger entry exists
    $checkLedger = $conn->prepare("SELECT id FROM registration_ledger WHERE order_id = 50");
    $checkLedger->execute();
    $ledger = $checkLedger->fetch();

    if (!$ledger) {
        // Create it
        $ins = $conn->prepare("INSERT INTO registration_ledger (order_id, customer_id, customer_name, bike_name, physical_bike_id, step_1_status) 
                              VALUES (50, 1, 'max', 'Honda CB', :bid, 'pending')");
        $ins->execute([':bid' => $bikeId]);
        $message = "Created new ledger entry for ORD-50 and linked to bike ID $bikeId";
    } else {
        // Update it
        $upd = $conn->prepare("UPDATE registration_ledger SET physical_bike_id = :bid WHERE order_id = 50");
        $upd->execute([':bid' => $bikeId]);
        $message = "Updated existing ledger entry for ORD-50 with bike ID $bikeId";
    }

    echo json_encode(["success" => true, "message" => $message]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>
