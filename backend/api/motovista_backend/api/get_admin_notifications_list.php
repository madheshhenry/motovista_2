<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // 1. Get Persistent Notifications
    $stmtLog = $conn->query("SELECT id, title, message, type, is_read, 
                            CASE 
                                WHEN created_at >= NOW() - INTERVAL 1 HOUR THEN 'Just now'
                                WHEN created_at >= NOW() - INTERVAL 1 DAY THEN CONCAT(HOUR(TIMEDIFF(NOW(), created_at)), ' hours ago')
                                ELSE DATE_FORMAT(created_at, '%d %b %Y')
                            END as timestamp,
                            target_screen, item_id, created_at
                            FROM admin_notifications 
                            ORDER BY created_at DESC LIMIT 50");
    $logs = $stmtLog->fetchAll(PDO::FETCH_ASSOC);

    // 2. Get Pending EMI Verifications (formatted to match)
    $stmtEmi = $conn->query("SELECT ep.id, 
                            CONCAT('EMI: ', cr.bike_name) as title, 
                            CONCAT(cr.customer_name, ' paid ₹', ep.amount_paid) as message, 
                            'emi' as type, 0 as is_read,
                            CASE 
                                WHEN ep.created_at >= NOW() - INTERVAL 1 HOUR THEN 'Just now'
                                WHEN ep.created_at >= NOW() - INTERVAL 1 DAY THEN CONCAT(HOUR(TIMEDIFF(NOW(), ep.created_at)), ' hours ago')
                                ELSE DATE_FORMAT(ep.created_at, '%d %b %Y')
                            END as timestamp,
                            'EmiDetailsActivity' as target_screen, 
                            ep.ledger_id as item_id, 
                            ep.created_at
                            FROM emi_payments ep
                            JOIN emi_ledgers el ON ep.ledger_id = el.id
                            JOIN customer_requests cr ON el.request_id = cr.id
                            WHERE ep.status = 'pending'
                            ORDER BY ep.created_at DESC");
    $emis = $stmtEmi->fetchAll(PDO::FETCH_ASSOC);

    // Merge and Sort
    $all = array_merge($logs, $emis);
    usort($all, function ($a, $b) {
        return strtotime($b['created_at']) - strtotime($a['created_at']);
    });

    echo json_encode([
        "success" => true,
        "data" => $all
    ]);

} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Error fetching notifications: " . $e->getMessage()]);
}
?>