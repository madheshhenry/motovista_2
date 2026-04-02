<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // We join bikes with registration_ledger (to link to order) 
    // and customer_requests (to get price and payment details)
    $sql = "SELECT 
                b.id as bike_id, 
                b.brand, 
                b.model, 
                b.variant, 
                b.colors as bike_color, 
                b.engine_number, 
                b.chassis_number,
                rl.order_id, 
                rl.customer_name, 
                rl.created_at as sale_date,
                cr.customer_id,
                cr.bike_price as total_value, 
                cr.status as request_status,
                cr.customer_phone,
                CONCAT(u.house_no, ', ', u.street, ', ', u.city, ', ', u.state, ' - ', u.pincode) as customer_address,
                CASE WHEN el.id IS NOT NULL THEN 'EMI' ELSE 'Cash' END as payment_type
            FROM registration_ledger rl
            LEFT JOIN bikes b ON rl.physical_bike_id = b.id
            JOIN customer_requests cr ON rl.order_id = cr.id
            LEFT JOIN users u ON cr.customer_id = u.id
            LEFT JOIN emi_ledgers el ON cr.id = el.request_id
            WHERE cr.status IN ('completed', 'delivered')
            ORDER BY rl.created_at DESC";

    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $sales = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Format the data for the frontend
    foreach ($sales as &$sale) {
        $sale['bike_id'] = (int)$sale['bike_id'];
        $sale['order_id'] = (int)$sale['order_id'];
        $sale['customer_id'] = (int)$sale['customer_id'];
        
        // Format date for better display
        $date = new DateTime($sale['sale_date']);
        $sale['formatted_date'] = $date->format('M d, Y');
        
        // Clean up color (Brand|Hex)
        if (strpos($sale['bike_color'], '|') !== false) {
            $sale['bike_color_name'] = explode('|', $sale['bike_color'])[0];
        } else {
            $sale['bike_color_name'] = $sale['bike_color'];
        }
    }

    echo json_encode(["success" => true, "data" => $sales]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>
