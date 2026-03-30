<?php
header("Content-Type: application/json; charset=UTF-8");
ini_set('display_errors', 0);
ini_set('log_errors', 1);
ini_set('error_log', '../php_error.log');

try {
    if (!file_exists('../config/db_connect.php'))
        throw new Exception("Config file db_connect.php not found");
    require_once '../config/db_connect.php';

    if (!file_exists('../config/jwt_helper.php'))
        throw new Exception("Config file jwt_helper.php not found");
    require_once '../config/jwt_helper.php';

    // Get Token
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';
    if (preg_match('/Bearer\s(\S+)/', $authHeader, $matches)) {
        $token = $matches[1];
    } else {
        $token = $authHeader;
    }

    if (empty($token)) {
        echo json_encode(["success" => false, "message" => "Unauthorized"]);
        exit;
    }

    // Validate Token
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);

    if (!$payload || !isset($payload['user_id'])) {
        echo json_encode(["success" => false, "message" => "Invalid session"]);
        exit;
    }

    $user_id = $payload['user_id'];

    // Fetch ledgers for this specific user
    // We join emi_ledgers with customer_requests on request_id
    // And filter by customer_requests.customer_id
    $sql = "SELECT el.*, 
                   cr.customer_name, cr.customer_phone, cr.customer_profile,
                   cr.bike_name as vehicle_name,
                   cr.bike_color, cr.bike_variant
            FROM emi_ledgers el
            JOIN customer_requests cr ON el.request_id = cr.id
            WHERE cr.customer_id = :uid
            ORDER BY el.created_at DESC";

    $stmt = $conn->prepare($sql);
    $stmt->execute([':uid' => $user_id]);
    $ledgers = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Format numbers
    foreach ($ledgers as &$ledger) {
        $ledger['total_amount'] = number_format($ledger['total_amount'], 2, '.', '');
        $ledger['paid_amount'] = number_format($ledger['paid_amount'], 2, '.', '');
        $ledger['remaining_amount'] = number_format($ledger['remaining_amount'], 2, '.', '');
        $ledger['emi_monthly_amount'] = number_format($ledger['emi_monthly_amount'], 2, '.', '');
    }

    echo json_encode(["success" => true, "data" => $ledgers]);

} catch (Exception $e) {
    http_response_code(200); // Return 200 with error message
    echo json_encode(["success" => false, "message" => "Server Error: " . $e->getMessage()]);
}
?>