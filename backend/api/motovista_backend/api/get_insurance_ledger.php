<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // Join with customer_requests to get phone number if needed
    $sql = "SELECT il.*, cr.customer_phone as phone
            FROM insurance_ledger il
            LEFT JOIN customer_requests cr ON il.order_id = cr.id
            ORDER BY il.created_at DESC";

    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode(["success" => true, "data" => $data]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>