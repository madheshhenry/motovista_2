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

    // 2. Fetch All Bikes (New & Second Hand)
    // We select relevant columns to display in the list
    // Note: 'price' is for Second Hand, 'on_road_price' is for New
    $sql = "SELECT 
                id, 
                brand, 
                model, 
                variant, 
                year, 
                condition_type, 
                on_road_price, 
                price as second_hand_price,
                image_paths, 
                created_at, 
                CASE 
                    WHEN condition_type = 'NEW' THEN on_road_price 
                    ELSE price 
                END as final_display_price
            FROM bikes 
            ORDER BY created_at DESC";

    $stmt = $conn->query($sql);
    $bikes = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 3. Process Images
    foreach ($bikes as &$bike) {
        $images = json_decode($bike['image_paths'], true);
        $bike['image_paths'] = is_array($images) ? $images : [];

        // Add a thumbnail helper
        if (!empty($bike['image_paths'])) {
            $bike['thumbnail'] = $bike['image_paths'][0];
        } else {
            $bike['thumbnail'] = null;
        }
    }

    echo json_encode([
        "status" => true,
        "data" => $bikes
    ]);

} catch (Exception $e) {
    http_response_code(401);
    echo json_encode([
        "status" => false,
        "message" => $e->getMessage()
    ]);
}
?>