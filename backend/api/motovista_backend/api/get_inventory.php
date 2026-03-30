<?php
header("Content-Type: application/json; charset=UTF-8");

require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

try {
    // 1. Verify Admin Token
    $headers = array_change_key_case(apache_request_headers(), CASE_LOWER);
    $authHeader = $headers['authorization'] ?? '';

    if (!$authHeader) {
        $authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
    }

    if (!$authHeader) {
        throw new Exception("Authorization token missing");
    }

    $token = str_replace('Bearer ', '', $authHeader);
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);

    if (!$payload) {
        throw new Exception("Unauthorized access or invalid token");
    }

    // 2. Fetch All New Bikes Ordered by Brand (Include Sold ones for history)
    $sql = "SELECT id, brand, model, variant, engine_number, chassis_number, colors, image_paths, date,
                   status, customer_name, sold_date 
            FROM bikes 
            WHERE condition_type = 'NEW' 
            ORDER BY brand ASC, model ASC";

    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $bikes = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 3. Group by Brand
    $inventory = [];

    foreach ($bikes as $bike) {
        $brandName = $bike['brand'];

        // Find existing brand entry in our inventory array
        $brandIndex = -1;
        foreach ($inventory as $index => $item) {
            if ($item['brand'] === $brandName) {
                $brandIndex = $index;
                break;
            }
        }

        // Process bike data for display
        $bikeData = [
            'id' => $bike['id'],
            'model' => $bike['model'],
            'variant' => $bike['variant'],
            'engine_number' => $bike['engine_number'] ?? 'N/A',
            'chassis_number' => $bike['chassis_number'] ?? 'N/A',
            'stock_date' => $bike['date'] ?? 'N/A',
            'status' => $bike['status'] ?? 'Available',
            'customer_name' => $bike['customer_name'],
            'sold_date' => $bike['sold_date'],
            'colors' => $bike['colors'],
            // Get first image for image_url if needed
            'image_url' => ''
        ];

        // extract first image
        $images = json_decode($bike['image_paths'], true);
        if (is_array($images) && count($images) > 0) {
            $bikeData['image_url'] = $images[0];
        }

        if ($brandIndex > -1) {
            // Add to existing brand category
            $inventory[$brandIndex]['bikes'][] = $bikeData;
            $inventory[$brandIndex]['count']++;
        } else {
            // Create new brand category
            $inventory[] = [
                'brand' => $brandName,
                'count' => 1,
                'bikes' => [$bikeData]
            ];
        }
    }

    echo json_encode([
        "status" => true,
        "message" => "Inventory fetched successfully",
        "data" => $inventory
    ]);

} catch (Exception $e) {
    http_response_code(200);
    echo json_encode([
        "status" => false,
        "message" => $e->getMessage(),
        "data" => []
    ]);
}
?>