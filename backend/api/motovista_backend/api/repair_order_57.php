<?php
require_once '../config/db_connect.php';

try {
    $orderId = 57;
    $bikeId = 48; // The next available Yamaha R13

    // 1. Assign Bike 48 to max (Order 57)
    $stmt1 = $conn->prepare("UPDATE bikes SET status = 'Sold', sold_date = NOW(), customer_name = 'max' WHERE id = ?");
    $stmt1->execute([$bikeId]);
    echo "Bike $bikeId assigned to max.\n";

    // 2. Fetch order details
    $stmt2 = $conn->prepare("SELECT customer_id, customer_name, bike_name FROM customer_requests WHERE id = ?");
    $stmt2->execute([$orderId]);
    $order = $stmt2->fetch(PDO::FETCH_ASSOC);

    if ($order) {
        // 3. Create Ledger Entry
        $stmt3 = $conn->prepare("INSERT INTO registration_ledger (order_id, customer_id, customer_name, bike_name, physical_bike_id, step_1_status, step_2_status, step_3_status, step_4_status) 
                                 VALUES (?, ?, ?, ?, ?, 'pending', 'locked', 'locked', 'locked')");
        $stmt3->execute([
            $orderId, 
            $order['customer_id'], 
            $order['customer_name'], 
            $order['bike_name'], 
            $bikeId
        ]);
        echo "Ledger record created for Order $orderId.\n";
    }

} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>
