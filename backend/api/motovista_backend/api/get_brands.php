<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

try {
    // 1. Verify Admin Token
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';
    if (!$authHeader)
        throw new Exception("Authorization token missing");

    $token = str_replace('Bearer ', '', $authHeader);
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);
    if (!$payload)
        throw new Exception("Unauthorized access");

    // 2. Fetch Brands (Also count bikes for each brand)
    // Left Join with bikes to get count
    // 2. Fetch Brands (Also count bikes for each brand)
    // We subquery both tables to get counts per brand
    $sql = "
    SELECT b.id, b.brand_name, b.brand_logo, 
           (
               (SELECT COUNT(*) FROM bikes WHERE brand = b.brand_name AND condition_type = 'NEW') + 
               (SELECT COUNT(*) FROM stock_bikes WHERE brand = b.brand_name AND condition_type = 'NEW')
           ) as bike_count 
    FROM brands b 
    ORDER BY b.brand_name ASC";

    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $brands = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Format for Android
    $formattedBrands = [];
    foreach ($brands as $brand) {
        $formattedBrands[] = [
            'brand' => $brand['brand_name'],
            'logo' => $brand['brand_logo'],
            'count' => $brand['bike_count'],
            'bikes' => [] // We don't fetch all bikes here anymore to keep it light, or we can fetch if needed? 
            // The User wants "Separate function", so listing brands is primary.
            // Clicking a brand will fetch bikes for that brand separately (BrandBikesActivity).
        ];
    }

    echo json_encode([
        "status" => true,
        "message" => "Brands fetched successfully",
        "data" => $formattedBrands
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