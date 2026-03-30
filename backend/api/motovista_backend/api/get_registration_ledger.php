<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // We join registration_ledger with customer_requests to get the phone number
    // And we join with bikes to get physical details like engine number
    // We prefer the accurate physical_bike_id link, falling back to name match with GROUP BY to avoid duplicates
    $sql = "SELECT rl.*, cr.customer_phone as phone, b.engine_number, b.variant, b.colors as bike_color
            FROM registration_ledger rl
            LEFT JOIN customer_requests cr ON rl.order_id = cr.id
            LEFT JOIN bikes b ON (
                (rl.physical_bike_id IS NOT NULL AND b.id = rl.physical_bike_id)
                OR
                (rl.physical_bike_id IS NULL AND b.customer_name = rl.customer_name AND b.model = rl.bike_name AND b.status = 'Sold')
            )
            GROUP BY rl.id
            ORDER BY rl.created_at DESC";
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode(["success" => true, "data" => $data]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>