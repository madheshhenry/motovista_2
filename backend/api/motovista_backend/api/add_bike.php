<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    // 1. Authorization
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';
    if (!$authHeader)
        throw new Exception("Authorization token missing");

    $token = str_replace('Bearer ', '', $authHeader);
    $payload = (new JWT_HELPER())->validateToken($token);
    if (!$payload)
        throw new Exception("Unauthorized access");

    // 2. Decode Input
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!$data || !isset($data['model']) || !isset($data['variants'])) {
        throw new Exception("Invalid Data Structure");
    }

    $model = $data['model'];
    $variants = $data['variants'];

    // 3. Start Transaction
    $conn->beginTransaction();

    // 4. Insert Model
    $sqlModel = "INSERT INTO bike_models (
        brand, model_name, model_year, engine_cc, fuel_type, transmission, 
        mileage, fuel_tank_capacity, kerb_weight, seat_height, ground_clearance,
        max_torque, max_power, warranty_period, free_services,
        invoice_legal_notes, mandatory_fittings, additional_fittings
    ) VALUES (
        :brand, :model_name, :model_year, :engine_cc, :fuel_type, :transmission,
        :mileage, :fuel_tank_capacity, :kerb_weight, :seat_height, :ground_clearance,
        :max_torque, :max_power, :warranty_period, :free_services,
        :invoice_legal_notes, :mandatory_fittings, :additional_fittings
    )";

    $stmtModel = $conn->prepare($sqlModel);

    $stmtModel->execute([
        ':brand' => $model['brand'],
        ':model_name' => $model['model_name'],
        ':model_year' => $model['model_year'],
        ':engine_cc' => $model['engine_cc'],
        ':fuel_type' => $model['fuel_type'],
        ':transmission' => $model['transmission'],
        ':mileage' => $model['mileage'],
        ':fuel_tank_capacity' => $model['fuel_tank_capacity'],
        ':kerb_weight' => $model['kerb_weight'],
        ':seat_height' => $model['seat_height'],
        ':ground_clearance' => $model['ground_clearance'],
        ':max_torque' => $model['max_torque'],
        ':max_power' => $model['max_power'],
        ':warranty_period' => $model['warranty_period'],
        ':free_services' => $model['free_services'],
        ':invoice_legal_notes' => json_encode($model['invoice_legal_notes']),
        ':mandatory_fittings' => json_encode($model['mandatory_fittings'] ?? []),
        ':additional_fittings' => json_encode($model['additional_fittings'] ?? [])
    ]);

    $modelId = $conn->lastInsertId();

    // 5. Insert Variants
    $sqlVariant = "INSERT INTO bike_variants (
        model_id, variant_name, price_details, brakes_wheels, colors, custom_sections
    ) VALUES (
        :model_id, :variant_name, :price_details, :brakes_wheels, :colors, :custom_sections
    )";

    $stmtVariant = $conn->prepare($sqlVariant);

    foreach ($variants as $variant) {
        $stmtVariant->execute([
            ':model_id' => $modelId,
            ':variant_name' => strtoupper($variant['variant_name']),
            ':price_details' => json_encode($variant['price_details']),
            ':brakes_wheels' => json_encode($variant['brakes_wheels']),
            ':colors' => json_encode($variant['colors']),
            ':custom_sections' => json_encode($variant['custom_sections'])
        ]);
    }

    // 6. Commit
    $conn->commit();

    echo json_encode([
        "status" => "success",
        "success" => true,
        "message" => "Bike and Variants added successfully"
    ]);

} catch (Exception $e) {
    if (isset($conn) && $conn->inTransaction()) {
        $conn->rollBack();
    }
    http_response_code(400); // Bad Request or appropriate error code
    echo json_encode([
        "status" => "error",
        "success" => false, 
        "message" => $e->getMessage()
    ]);
}
?>