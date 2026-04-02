<?php
require_once '../config/db_connect.php';

try {
    echo "Starting Data Repair...\n";

    // 1. Find completed orders that are missing bike links in the ledger
    $sql = "SELECT id, bike_name, customer_name, customer_id FROM customer_requests WHERE status IN ('completed', 'delivered')";
    $stmt = $conn->query($sql);
    $orders = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $repairedCount = 0;

    foreach ($orders as $order) {
        $requestId = $order['id'];
        $model = trim($order['bike_name']);
        
        // 2. Try to find a matching bike for this order
        $findS = $conn->prepare("SELECT id FROM bikes WHERE (status = 'Available' OR (status = 'Sold' AND customer_name = :cn)) AND (LOWER(REPLACE(model, ' ', '')) = LOWER(REPLACE(:model, ' ', '')) OR model LIKE :ml) LIMIT 1");
        $findS->execute([
            ':cn' => $order['customer_name'],
            ':model' => $model,
            ':ml' => '%' . $model . '%'
        ]);
        $bike = $findS->fetch(PDO::FETCH_ASSOC);

        if ($bike) {
            $bikeId = $bike['id'];
            
            // 3. Mark bike as Sold if it wasn't already
            $upB = $conn->prepare("UPDATE bikes SET status = 'Sold', customer_name = :cn, sold_date = NOW() WHERE id = :bid");
            $upB->execute([':cn' => $order['customer_name'], ':bid' => $bikeId]);

            // 4. Update ledger
            $upL = $conn->prepare("UPDATE registration_ledger SET physical_bike_id = :bid WHERE order_id = :oid");
            $upL->execute([':bid' => $bikeId, ':oid' => $requestId]);

            $repairedCount++;
            echo "Linked Order #$requestId to Bike #$bikeId\n";
        }
    }

    echo "Finished! Repaired $repairedCount records.\n";

} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>
