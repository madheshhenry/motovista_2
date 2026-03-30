<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // Ideally we should check Admin Auth here

    // Fetch all ledgers with basic details
    $sql = "SELECT el.*, 
                   cr.customer_name, cr.customer_phone, cr.customer_profile,
                   cr.bike_name as vehicle_name,
                   cr.bike_color, cr.bike_variant
            FROM emi_ledgers el
            LEFT JOIN customer_requests cr ON el.request_id = cr.id
            ORDER BY el.created_at DESC";

    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $ledgers = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Process image paths if needed
    foreach ($ledgers as &$ledger) {
        if (isset($ledger['customer_profile'])) {
            // ensure full path if needed, or client handles it
        }

        // Formatting numbers
        $ledger['total_amount'] = number_format($ledger['total_amount'], 2, '.', '');
        $ledger['paid_amount'] = number_format($ledger['paid_amount'], 2, '.', '');
        $ledger['remaining_amount'] = number_format($ledger['remaining_amount'], 2, '.', '');
        $ledger['emi_monthly_amount'] = number_format($ledger['emi_monthly_amount'], 2, '.', '');
    }

    echo json_encode(["success" => true, "data" => $ledgers]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>