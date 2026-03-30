<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    if (!isset($_GET['customer_id'])) {
        throw new Exception("Customer ID is required");
    }
    $customerId = intval($_GET['customer_id']);

    $sql = "SELECT id, bike_name, bike_variant, bike_color, bike_price, status, created_at 
            FROM customer_requests 
            WHERE customer_id = :cid 
            ORDER BY created_at DESC";

    $stmt = $conn->prepare($sql);
    $stmt->execute([':cid' => $customerId]);
    $orders = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Process color hex
    foreach ($orders as &$order) {
        // Logic similar to get_order_summary for color name/hex if needed
        // For list view, we might correct it here too or leave as is if frontend handles it
    }

    echo json_encode(["success" => true, "data" => $orders]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>