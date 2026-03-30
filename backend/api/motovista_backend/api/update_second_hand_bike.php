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

    if (!$data || !isset($data['id'])) {
        throw new Exception("Invalid JSON input or missing ID");
    }

    // 3. Prepare SQL
    $sql = "UPDATE bikes SET 
        brand = :brand,
        model = :model,
        year = :year,
        price = :price,
        engine_cc = :engine_cc,
        condition_type = :condition_type,
        ownership = :ownership,
        braking_type = :braking_type,
        owner_details = :owner_details,
        condition_details = :condition_details,
        features = :features,
        image_paths = :image_paths,
        odometer = :odometer
    WHERE id = :id";

    $stmt = $conn->prepare($sql);

    // 4. Bind Values
    $params = [
        ':id' => $data['id'],
        ':brand' => $data['brand'] ?? '',
        ':model' => $data['model'] ?? '',
        ':year' => $data['year'] ?? '',
        ':price' => $data['price'] ?? '',
        ':engine_cc' => $data['engine_cc'] ?? '',
        ':condition_type' => $data['condition_type'] ?? 'Second Hand',
        ':ownership' => $data['ownership'] ?? '',
        ':braking_type' => $data['braking_type'] ?? '',
        ':owner_details' => $data['owner_details'] ?? '',
        ':condition_details' => $data['condition_details'] ?? '',
        ':features' => $data['features'] ?? '',
        ':image_paths' => $data['image_paths'] ?? '[]',
        ':odometer' => $data['odometer'] ?? ''
    ];

    $stmt->execute($params);

    if ($stmt->rowCount() > 0) {
        echo json_encode([
            "status" => "success",
            "message" => "Bike updated successfully"
        ]);
    } else {
        // Warning: rowCount returns 0 if data is identical
        echo json_encode([
            "status" => "success",
            "message" => "Bike updated (or no changes detected)"
        ]);
    }

} catch (Exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}
?>