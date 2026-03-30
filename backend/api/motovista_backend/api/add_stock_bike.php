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

    if (!$authHeader)
        throw new Exception("Authorization token missing");

    $token = str_replace('Bearer ', '', $authHeader);
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);
    // if (!$payload)
    //    throw new Exception("Unauthorized access");

    // 2. Ensure Table Exists (Skipped - using main `bikes` table)
    // $createTable = "..."; 
    // $conn->exec($createTable);

    // 3. Get Input Data
    $brand = $_POST['brand'] ?? '';
    $model = $_POST['model'] ?? '';
    $variant = $_POST['variant'] ?? '';
    $colors = $_POST['colors'] ?? '';
    $engine_number = $_POST['engine_number'] ?? '';
    $chassis_number = $_POST['chassis_number'] ?? '';
    $date = $_POST['date'] ?? '';

    // 4. Validation
    if (empty($brand) || empty($model) || empty($engine_number) || empty($chassis_number)) {
        throw new Exception("Brand, Model, Engine Number, and Chassis Number are required.");
    }

    // 5. Check for duplicate Engine/Chassis (Global check, not just in batch)
    $checkSql = "SELECT id FROM bikes WHERE engine_number = :eng OR chassis_number = :chs";
    $checkStmt = $conn->prepare($checkSql);
    $checkStmt->execute([':eng' => $engine_number, ':chs' => $chassis_number]);
    if ($checkStmt->rowCount() > 0) {
        throw new Exception("Bike with this Engine or Chassis number already exists completely.");
    }

    // 6. Insert
    $sql = "INSERT INTO bikes (brand, model, variant, colors, engine_number, chassis_number, date, condition_type, image_paths, status, created_at) 
            VALUES (:brand, :model, :variant, :colors, :engine_number, :chassis_number, :date, 'NEW', '[]', 'Available', NOW())";

    $stmt = $conn->prepare($sql);
    $params = [
        ':brand' => $brand,
        ':model' => $model,
        ':variant' => $variant,
        ':colors' => $colors,
        ':engine_number' => $engine_number,
        ':chassis_number' => $chassis_number,
        ':date' => $date
    ];

    if ($stmt->execute($params)) {
        echo json_encode([
            "status" => true,
            "message" => "Stock Bike added successfully"
        ]);
    } else {
        throw new Exception("Failed to save stock bike.");
    }

} catch (Exception $e) {
    http_response_code(200);
    echo json_encode([
        "status" => false,
        "message" => $e->getMessage()
    ]);
}
?>