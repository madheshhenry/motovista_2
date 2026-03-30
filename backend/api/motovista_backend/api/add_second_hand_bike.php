<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

// Disable error display
ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    // 1. Verify Admin Token
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';

    if (!$authHeader) {
        throw new Exception("Authorization token missing");
    }

    $token = str_replace('Bearer ', '', $authHeader);
    $payload = (new JWT_HELPER())->validateToken($token);

    if (!$payload || !isset($payload['role']) || $payload['role'] !== 'admin') {
        throw new Exception("Unauthorized access");
    }

    // 2. Get JSON Input
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!$data) {
        throw new Exception("Invalid JSON input");
    }

    // 3. Prepare SQL
    $sql = "INSERT INTO bikes (
        brand, model, year, price, engine_cc, 
        condition_type, ownership, braking_type, 
        owner_details, condition_details, features, 
        image_paths, odometer, created_at
    ) VALUES (
        :brand, :model, :year, :price, :engine_cc, 
        :condition_type, :ownership, :braking_type, 
        :owner_details, :condition_details, :features, 
        :image_paths, :odometer, NOW()
    )";

    $stmt = $conn->prepare($sql);

    // 4. Bind Values
    $params = [
        ':brand' => $data['brand'] ?? '',
        ':model' => $data['model'] ?? '',
        ':year' => $data['year'] ?? '',
        ':price' => $data['price'] ?? '',
        ':engine_cc' => $data['engine_cc'] ?? '',
        ':condition_type' => $data['condition_type'] ?? 'Second Hand', // Use 'Second Hand' or whatever filtering uses
        ':ownership' => $data['ownership'] ?? '',
        ':braking_type' => $data['braking_type'] ?? '',
        ':owner_details' => $data['owner_details'] ?? '',
        ':condition_details' => $data['condition_details'] ?? '',
        ':features' => $data['features'] ?? '',
        ':image_paths' => $data['image_paths'] ?? '[]',
        ':odometer' => $data['odometer'] ?? ''
    ];

    $stmt->execute($params);

    echo json_encode([
        "status" => "success",
        "message" => "Second hand bike added successfully",
        "bike_id" => $conn->lastInsertId()
    ]);

} catch (Exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}
?>