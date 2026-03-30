<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    if (!isset($_GET['id'])) {
        throw new Exception("Bike ID is required");
    }
    $bikeId = intval($_GET['id']);

    // 1. Authorization
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';
    if (!$authHeader)
        throw new Exception("Authorization token missing");

    $token = str_replace('Bearer ', '', $authHeader);
    $payload = (new JWT_HELPER())->validateToken($token);
    if (!$payload)
        throw new Exception("Unauthorized access");

    // 2. Fetch Model
    $stmt = $conn->prepare("SELECT * FROM bike_models WHERE id = :id");
    $stmt->execute([':id' => $bikeId]);
    $model = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$model) {
        throw new Exception("Bike Model not found");
    }

    // Decode Model JSON fields
    $model['invoice_legal_notes'] = json_decode($model['invoice_legal_notes']);
    $model['mandatory_fittings'] = json_decode($model['mandatory_fittings']);
    $model['additional_fittings'] = json_decode($model['additional_fittings']);
    $model['showroom_bank_details'] = json_decode($model['showroom_bank_details']);

    // 3. Fetch Variants
    $stmtVar = $conn->prepare("SELECT * FROM bike_variants WHERE model_id = :mid");
    $stmtVar->execute([':mid' => $bikeId]);
    $variantsRaw = $stmtVar->fetchAll(PDO::FETCH_ASSOC);

    $variants = [];
    foreach ($variantsRaw as $v) {
        // Decode Variant JSON fields
        $v['price_details'] = json_decode($v['price_details']);
        $v['brakes_wheels'] = json_decode($v['brakes_wheels']);
        $v['colors'] = json_decode($v['colors']);
        $v['custom_sections'] = json_decode($v['custom_sections']);
        $variants[] = $v;
    }

    echo json_encode([
        "status" => "success",
        "data" => [
            "model" => $model,
            "variants" => $variants
        ]
    ]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}
?>