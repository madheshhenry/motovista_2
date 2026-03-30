<?php
require_once '../config/db_connect.php';
header('Content-Type: application/json');

try {
    $stmt = $conn->query("SELECT * FROM customer_notifications ORDER BY created_at DESC LIMIT 10");
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $count = $conn->query("SELECT COUNT(*) FROM customer_notifications")->fetchColumn();

    echo json_encode([
        "total_count" => $count,
        "recent_notifications" => $data
    ]);
} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>