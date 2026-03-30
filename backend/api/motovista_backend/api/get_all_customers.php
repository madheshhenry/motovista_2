<?php
header("Content-Type: application/json; charset=UTF-8");

require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

try {
    // 1. Verify Admin Token
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';

    if (!$authHeader) {
        throw new Exception("Authorization token missing");
    }

    $token = str_replace('Bearer ', '', $authHeader);
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);

    if (!$payload) {
        throw new Exception("Unauthorized access");
    }

    // Optional: Check if user is admin (if you have role in token)
    // if ($payload['role'] !== 'admin') { ... }

    // 2. Fetch Customers
    $sql = "SELECT id, full_name, email, phone, status, created_at FROM customers ORDER BY created_at DESC";
    $stmt = $conn->prepare($sql);
    $stmt->execute();

    $customers = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        "status" => true,
        "message" => "Customers fetched successfully",
        "data" => $customers
    ]);

} catch (Exception $e) {
    http_response_code(400); // Bad Request or Unauthorized
    echo json_encode([
        "status" => false,
        "message" => $e->getMessage(),
        "data" => []
    ]);
}
?>