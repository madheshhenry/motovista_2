<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // 1. Persistent Unread Notifications (Logs/System alerts)
    $stmtLogs = $conn->query("SELECT COUNT(*) as count FROM admin_notifications WHERE is_read = 0");
    $logCount = $stmtLogs->fetch(PDO::FETCH_ASSOC)['count'];

    // 2. Pending EMI Verifications
    $stmtEmi = $conn->query("SELECT COUNT(*) as count FROM emi_payments WHERE status = 'pending'");
    $emiCount = $stmtEmi->fetch(PDO::FETCH_ASSOC)['count'];

    // 3. Pending Customer Requests
    $stmtReq = $conn->query("SELECT COUNT(*) as count FROM customer_requests WHERE status = 'pending'");
    $reqCount = $stmtReq->fetch(PDO::FETCH_ASSOC)['count'];

    // 4. New Bike Requests
    $stmtBike = $conn->query("SELECT COUNT(*) as count FROM bike_requests WHERE status = 'pending'");
    $bikeCount = $stmtBike->fetch(PDO::FETCH_ASSOC)['count'];

    // The badge total should only reflect items visible in the "Notification Screen"
    // to avoid confusion (where badge shows X but screen shows Y).
    // Application/Bike requests are seen in separate dashboard cards.
    $visibleNotificationTotal = (int)$logCount + (int)$emiCount;

    echo json_encode([
        "success" => true,
        "counts" => [
            "system_logs" => (int) $logCount,
            "emi_verifications" => (int) $emiCount,
            "customer_requests" => (int) $reqCount,
            "bike_requests" => (int) $bikeCount,
            "total" => $visibleNotificationTotal
        ]
    ]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["status" => false, "message" => $e->getMessage()]);
}
?>