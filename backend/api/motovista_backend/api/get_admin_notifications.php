<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // 1. Pending EMI Verifications
    $stmtEmi = $conn->query("SELECT COUNT(*) as count FROM emi_payments WHERE status = 'pending'");
    $emiCount = $stmtEmi->fetch(PDO::FETCH_ASSOC)['count'];

    // 2. Pending Customer Requests (If applicable)
    $stmtReq = $conn->query("SELECT COUNT(*) as count FROM customer_requests WHERE status = 'pending'");
    $reqCount = $stmtReq->fetch(PDO::FETCH_ASSOC)['count'];

    // 3. New Bike Requests (If applicable)
    $stmtBike = $conn->query("SELECT COUNT(*) as count FROM bike_requests WHERE status = 'pending'");
    $bikeCount = $stmtBike->fetch(PDO::FETCH_ASSOC)['count'];

    echo json_encode([
        "success" => true,
        "counts" => [
            "emi_verifications" => (int) $emiCount,
            "customer_requests" => (int) $reqCount,
            "bike_requests" => (int) $bikeCount,
            "total" => (int) ($emiCount + $reqCount + $bikeCount)
        ]
    ]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["status" => false, "message" => $e->getMessage()]);
}
?>