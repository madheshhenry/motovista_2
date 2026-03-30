<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    if (!isset($_GET['order_id'])) {
        throw new Exception("order_id is required");
    }

    $order_id = $_GET['order_id'];

    // Join with customer_requests to get request date and bike name
    // Join with customers to get profile image and full name
    $sql = "SELECT 
                il.*, 
                cr.created_at as registration_date,
                c.full_name,
                c.profile_image
            FROM insurance_ledger il
            LEFT JOIN customer_requests cr ON il.order_id = cr.id
            LEFT JOIN customers c ON il.customer_id = c.id
            WHERE il.order_id = ?
            LIMIT 1";

    $stmt = $conn->prepare($sql);
    $stmt->execute([$order_id]);
    $data = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$data) {
        throw new Exception("Insurance details not found for this order");
    }

    // Add full path to profile image if it exists
    if (!empty($data['profile_image'])) {
        // Assuming the base URL for profile pics is known or handled by the app
        // Just return the filename as other APIs do
    }

    echo json_encode(["success" => true, "data" => $data]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>