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

    // 2. Fetch input data
    $data = json_decode(file_get_contents("php://input"));
    
    // We expect both id and brand_name (or just id, but we need brand_name for cascading)
    if (!isset($data->brand_id)) {
        throw new Exception("Brand ID is required");
    }

    $brand_id = (int)$data->brand_id;

    // Fetch brand name before deletion to cascade deletes to bikes correctly
    $stmt = $conn->prepare("SELECT brand_name, brand_logo FROM brands WHERE id = ?");
    $stmt->execute([$brand_id]);
    $brand = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$brand) {
        throw new Exception("Brand not found");
    }

    $brand_name = $brand['brand_name'];
    $brand_logo = $brand['brand_logo'];

    // 3. Delete from database, but use transaction
    $conn->beginTransaction();

    // The user requested to delete ONLY new bikes. 
    // Usually new bikes have condition_type = 'NEW', second-hand are 'SECOND_HAND'.
    // Delete from bikes table where condition_type = 'NEW'
    $deleteBikesStmt = $conn->prepare("DELETE FROM bikes WHERE brand = ? AND condition_type = 'NEW'");
    $deleteBikesStmt->execute([$brand_name]);

    // Delete from stock_bikes where condition_type = 'NEW'
    $deleteStockBikesStmt = $conn->prepare("DELETE FROM stock_bikes WHERE brand = ? AND condition_type = 'NEW'");
    $deleteStockBikesStmt->execute([$brand_name]);

    // Delete the brand from the brands table
    $deleteBrandStmt = $conn->prepare("DELETE FROM brands WHERE id = ?");
    $deleteBrandStmt->execute([$brand_id]);

    $conn->commit();

    // 4. Cleanup Brand Logo from server
    if (!empty($brand_logo) && strpos($brand_logo, 'uploads/brands/') !== false) {
        $old_file_path = '../' . substr($brand_logo, strpos($brand_logo, 'uploads/brands/'));
        if (file_exists($old_file_path)) {
            unlink($old_file_path);
        }
    }

    echo json_encode([
        "status" => true,
        "message" => "Brand and associated new bikes deleted successfully"
    ]);

} catch (Exception $e) {
    if (isset($conn) && $conn->inTransaction()) {
        $conn->rollBack();
    }
    http_response_code(200);
    // Usually 200 for REST API errors handled gracefully
    echo json_encode([
        "status" => false,
        "message" => $e->getMessage()
    ]);
}
?>
