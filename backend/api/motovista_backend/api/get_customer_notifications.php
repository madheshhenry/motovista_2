<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

if (!isset($_GET['user_id'])) {
    echo json_encode(["success" => false, "message" => "user_id is required"]);
    exit;
}

$userId = $_GET['user_id'];

try {
    $stmt = $conn->prepare("SELECT id, title, message, type, is_read, 
                           CASE 
                               WHEN created_at >= NOW() - INTERVAL 1 HOUR THEN 'Just now'
                               WHEN created_at >= NOW() - INTERVAL 1 DAY THEN CONCAT(HOUR(TIMEDIFF(NOW(), created_at)), ' hours ago')
                               ELSE DATE_FORMAT(created_at, '%d %b %Y')
                           END as timestamp,
                           target_screen, item_id
                           FROM customer_notifications 
                           WHERE user_id = ? 
                           ORDER BY created_at DESC");
    $stmt->execute([$userId]);
    $notifications = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Convert is_read to boolean for GSON compatibility if needed, 
    // but PHP PDO usually returns it as string or int. 
    // We'll leave it for GSON to handle or cast here.
    foreach ($notifications as &$n) {
        $n['is_read'] = (bool) $n['is_read'];
    }

    echo json_encode([
        "success" => true,
        "data" => $notifications
    ]);

} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Error fetching notifications: " . $e->getMessage()]);
}
?>