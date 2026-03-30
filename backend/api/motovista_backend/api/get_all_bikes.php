<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

// Disable error display
ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    // 1. Verify Token
    $allHeaders = array_change_key_case(apache_request_headers(), CASE_LOWER);
    $authHeader = $allHeaders['authorization'] ?? '';

    if (!$authHeader) {
        $authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? '';
    }

    if (!$authHeader) {
        throw new Exception("Authorization token missing");
    }

    $token = str_replace('Bearer ', '', $authHeader);
    $payload = (new JWT_HELPER())->validateToken($token);

    if (!$payload) {
        throw new Exception("Unauthorized access");
    }

    // 2. Fetch All Bikes (Unified Inventory)
    // Dynamic check for price column
    $checkPrice = $conn->query("SHOW COLUMNS FROM bikes LIKE 'price'");
    $priceCol = $checkPrice->rowCount() > 0 ? "price" : "on_road_price";

    $sql = "SELECT 
                id, 
                brand, 
                model, 
                variant, 
                year, 
                condition_type, 
                on_road_price, 
                $priceCol as price,
                image_paths, 
                engine_cc,
                fuel_type,
                transmission,
                braking_type,
                mileage,
                features,
                created_at as date
            FROM bikes 
            ORDER BY created_at DESC";

    $stmt = $conn->query($sql);
    $bikes = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 3. Process Images and Types
    foreach ($bikes as &$bike) {
        // Handle images
        $images = json_decode($bike['image_paths'], true);
        $bike['image_paths'] = is_array($images) ? $images : [];

        if (!empty($bike['image_paths'])) {
            $bike['image_url'] = $bike['image_paths'][0];
        } else {
            $bike['image_url'] = null;
        }

        // Map condition_type to type for consistency if needed
        $bike['type'] = $bike['condition_type'];
    }

    echo json_encode([
        "success" => true,
        "status" => "success",
        "data" => $bikes
    ]);

} catch (Exception $e) {
    // Debug logging
    file_put_contents('debug_bikes_error.txt', date('[Y-m-d H:i:s] ') . $e->getMessage() . "\nHeaders: " . json_encode(apache_request_headers()) . "\n", FILE_APPEND);

    http_response_code(400);
    echo json_encode([
        "success" => false,
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}
?>