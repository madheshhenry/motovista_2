<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

try {
    // 1. Verify Admin Token (Optional strictness, keeping consistent with file)
    $headers = array_change_key_case(apache_request_headers(), CASE_LOWER);
    $authHeader = $headers['authorization'] ?? '';

    if (!$authHeader) {
        $authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
    }
    // if (!$authHeader) throw new Exception("Authorization token missing"); 

    // 2. Get Brand from Query
    $brand = isset($_GET['brand']) ? trim($_GET['brand']) : '';
    if (empty($brand)) {
        throw new Exception("Brand is required");
    }

    // 3. Fetch Bikes from ONLY 'bikes' table
    // We now have status, customer_name, sold_date directly in the table.
    $sql = "SELECT id, brand, model, variant, engine_number, chassis_number, 
                   image_paths, date AS stock_date, colors,
                   status, customer_name, sold_date
            FROM bikes 
            WHERE brand = :brand 
            AND condition_type = 'NEW' 
            ORDER BY model ASC, id DESC";

    $stmt = $conn->prepare($sql);
    $stmt->bindParam(':brand', $brand);
    $stmt->execute();
    $bikes = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $formattedBikes = [];
    foreach ($bikes as $bike) {
        $thumbnail = '';
        $images = json_decode($bike['image_paths'], true);
        if (is_array($images) && count($images) > 0) {
            $thumbnail = $images[0];
        }

        // Status Logic: direct from DB
        // DB status: 'Available', 'Sold'
        // Adapter expects: 'Sold' to trigger badge.
        $status = $bike['status'] ?? 'Available';

        $formattedBikes[] = [
            'id' => $bike['id'],
            'model' => $bike['model'],
            'variant' => $bike['variant'],
            'engine_number' => $bike['engine_number'],
            'chassis_number' => $bike['chassis_number'],
            'image_url' => $thumbnail,
            'stock_date' => $bike['stock_date'],
            'status' => $status,
            'customer_name' => $bike['customer_name'],
            'sold_date' => $bike['sold_date'], // Map to sold_date for Android Model
            'colors' => $bike['colors'],
            'source_table' => 'bikes'
        ];
    }

    echo json_encode([
        "success" => true,
        "message" => "Bikes fetched successfully",
        "data" => $formattedBikes
    ]);

} catch (Exception $e) {
    // Debug logging
    file_put_contents('debug_bikes_error.txt', date('[Y-m-d H:i:s] ') . "get_brand_bikes.php: " . $e->getMessage() . "\nHeaders: " . json_encode(apache_request_headers()) . "\n", FILE_APPEND);

    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => $e->getMessage(),
        "data" => []
    ]);
}
?>