<?php
header('Content-Type: text/plain');
require_once '../config/db_connect.php';

try {
    echo "=== NOTIFICATION COUNTS ===\n";
    
    $stmtLogs = $conn->query("SELECT COUNT(*) as count FROM admin_notifications WHERE is_read = 0");
    echo "Unread Logs (admin_notifications): " . $stmtLogs->fetchColumn() . "\n";

    $stmtEmi = $conn->query("SELECT COUNT(*) as count FROM emi_payments WHERE status = 'pending'");
    echo "Pending EMI Payments: " . $stmtEmi->fetchColumn() . "\n";

    $stmtReq = $conn->query("SELECT COUNT(*) as count FROM customer_requests WHERE status = 'pending'");
    echo "Pending Customer Requests: " . $stmtReq->fetchColumn() . "\n";

    $stmtBike = $conn->query("SELECT COUNT(*) as count FROM bike_requests WHERE status = 'pending'");
    echo "Pending Bike Requests: " . $stmtBike->fetchColumn() . "\n";

} catch (PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>
