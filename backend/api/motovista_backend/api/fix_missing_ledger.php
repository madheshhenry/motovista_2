<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

ini_set('display_errors', 1);
error_reporting(E_ALL);

try {
    // 1. Find all completed orders
    $sql = "SELECT id, customer_id, customer_name, bike_name FROM customer_requests WHERE status = 'completed'";
    $stmt = $conn->query($sql);
    $orders = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $fixedCount = 0;

    foreach ($orders as $order) {
        // 2. Check if ledger exists
        $checkSql = "SELECT id FROM registration_ledger WHERE order_id = :orderId";
        $checkStmt = $conn->prepare($checkSql);
        $checkStmt->execute([':orderId' => $order['id']]);

        if ($checkStmt->rowCount() == 0) {
            // 3. Create missing ledger entry
            $ledgerSql = "INSERT INTO registration_ledger (order_id, customer_id, customer_name, bike_name, step_1_status, step_2_status, step_3_status, step_4_status) 
                          VALUES (:orderId, :custId, :custName, :bikeName, 'pending', 'locked', 'locked', 'locked')";
            $ledgerStmt = $conn->prepare($ledgerSql);
            $ledgerStmt->execute([
                ':orderId' => $order['id'],
                ':custId' => $order['customer_id'],
                ':custName' => $order['customer_name'],
                ':bikeName' => $order['bike_name']
            ]);
            $fixedCount++;
        }
    }

    echo json_encode(["status" => "success", "fixed_count" => $fixedCount, "message" => "Fixed missing ledger entries"]);

} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>