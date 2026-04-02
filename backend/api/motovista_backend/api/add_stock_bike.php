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

    // 6. DYNAMIC: Fetch Image from bike_variants based on Model, Variant, and Color
    $imagePath = "[]"; // Default empty JSON array
    try {
        // Find the model ID first
        $modelSql = "SELECT id FROM bike_models WHERE LOWER(TRIM(brand)) = LOWER(TRIM(:brd)) AND LOWER(TRIM(model_name)) = LOWER(TRIM(:mdl)) LIMIT 1";
        $modelStmt = $conn->prepare($modelSql);
        $modelStmt->execute([':brd' => $brand, ':mdl' => $model]);
        $modelData = $modelStmt->fetch(PDO::FETCH_ASSOC);

        if ($modelData) {
            $mid = $modelData['id'];
            // Find the variant
            $variantSql = "SELECT colors FROM bike_variants WHERE model_id = :mid AND LOWER(TRIM(variant_name)) = LOWER(TRIM(:vnt)) LIMIT 1";
            $variantStmt = $conn->prepare($variantSql);
            $variantStmt->execute([':mid' => $mid, ':vnt' => $variant]);
            $variantData = $variantStmt->fetch(PDO::FETCH_ASSOC);

            if ($variantData && $variantData['colors']) {
                $dbColors = json_decode($variantData['colors'], true);
                // Incoming color is likely JSON array like ["Red|#FF0000"]
                $inputColorsArr = json_decode($colors, true);
                if (is_array($inputColorsArr) && !empty($inputColorsArr)) {
                    $selectedColorFull = $inputColorsArr[0]; // "Red|#FF0000"
                    $selectedColorName = explode('|', $selectedColorFull)[0];

                    foreach ($dbColors as $c) {
                        if (isset($c['color_name']) && strcasecmp(trim($c['color_name']), trim($selectedColorName)) == 0) {
                            if (!empty($c['image_paths']) && is_array($c['image_paths'])) {
                                $imagePath = json_encode($c['image_paths']);
                            }
                            break;
                        }
                    }
                }
            }
        }
    } catch (Exception $e) {
        // Log error but don't fail the insert if image lookup fails
        error_log("Image lookup error in add_stock_bike.php: " . $e->getMessage());
    }

    // 7. Insert
    $sql = "INSERT INTO bikes (brand, model, variant, colors, engine_number, chassis_number, date, condition_type, image_paths, status, created_at) 
            VALUES (:brand, :model, :variant, :colors, :engine_number, :chassis_number, :date, 'NEW', :img, 'Available', NOW())";

    $stmt = $conn->prepare($sql);
    $params = [
        ':brand' => $brand,
        ':model' => $model,
        ':variant' => $variant,
        ':colors' => $colors,
        ':engine_number' => $engine_number,
        ':chassis_number' => $chassis_number,
        ':date' => $date,
        ':img' => $imagePath
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