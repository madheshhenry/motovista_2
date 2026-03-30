<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

ini_set('display_errors', 0);
error_reporting(E_ALL);

function sendResponse($status, $message, $extra = [])
{
    echo json_encode(array_merge(["status" => $status, "message" => $message], $extra));
    exit;
}

try {
    // 1. Authorization
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

    // 2. Parse Input
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!$data) {
        throw new Exception("Invalid JSON input");
    }

    if (!isset($data['id'])) {
        throw new Exception("Bike ID is required");
    }
    $bikeId = $data['id'];

    // 3. Helper for JSON fields
    function processJsonField($input)
    {
        if (empty($input))
            return '[]';
        if (is_array($input))
            return json_encode($input);

        $decoded = json_decode($input, true);
        if (json_last_error() === JSON_ERROR_NONE && is_array($decoded)) {
            return $input;
        }

        // Comma-separated conversion
        $items = explode(',', $input);
        $cleanItems = array_map('trim', $items);
        $cleanItems = array_filter($cleanItems);
        return json_encode(array_values($cleanItems));
    }

    // 4. Update Query
    // Similar to INSERT, but UPDATE. Maps aliases correctly.
    $sql = "UPDATE bikes SET 
            brand = :brand, model = :model, variant = :variant, year = :year, 
            engine_cc = :engine_cc, fuel_type = :fuel_type, transmission = :transmission, 
            braking_type = :braking_type, on_road_price = :on_road_price, 
            ex_showroom_price = :ex_showroom_price, insurance_price = :insurance_price, 
            registration_price = :registration_price, ltrt_price = :ltrt_price,
            mileage = :mileage, fuel_tank_capacity = :fuel_tank_capacity, kerb_weight = :kerb_weight, 
            seat_height = :seat_height, ground_clearance = :ground_clearance,
            warranty = :warranty, free_services = :free_services, 
            registration_proof = :registration_proof, price_disclaimer = :price_disclaimer,
            condition_type = :condition_type, features = :features,
            date = :date, engine_number = :engine_number, chassis_number = :chassis_number,
            colors = :colors, color_images = :color_images, 
            custom_fittings = :custom_fittings, mandatory_fittings = :mandatory_fittings, additional_fittings = :additional_fittings,
            front_brake = :front_brake, rear_brake = :rear_brake, abs_type = :abs_type, wheel_type = :wheel_type,
            image_paths = :image_paths, max_torque = :max_torque
            WHERE id = :id";

    $stmt = $conn->prepare($sql);

    $params = [
        ':brand' => $data['brand'] ?? '',
        ':model' => $data['model'] ?? '',
        ':variant' => $data['variant'] ?? '',
        ':year' => $data['year'] ?? '',
        ':engine_cc' => $data['engine_cc'] ?? '',
        ':fuel_type' => $data['fuel_type'] ?? '',
        ':transmission' => $data['transmission'] ?? '',
        ':braking_type' => $data['braking_type'] ?? '',
        ':on_road_price' => $data['on_road_price'] ?? 0,
        ':ex_showroom_price' => $data['ex_showroom_price'] ?? 0,
        ':insurance_price' => $data['insurance_price'] ?? ($data['insurance'] ?? 0),
        ':registration_price' => $data['registration_charge'] ?? ($data['registration_price'] ?? 0),
        ':ltrt_price' => $data['ltrt'] ?? ($data['ltrt_price'] ?? 0),
        ':mileage' => $data['mileage'] ?? '',
        ':fuel_tank_capacity' => $data['fuel_tank_capacity'] ?? '',
        ':kerb_weight' => $data['kerb_weight'] ?? '',
        ':seat_height' => $data['seat_height'] ?? '',
        ':ground_clearance' => $data['ground_clearance'] ?? '',
        ':warranty' => $data['warranty_period'] ?? ($data['warranty'] ?? ''),
        ':free_services' => $data['free_services_count'] ?? ($data['free_services'] ?? ''),
        ':registration_proof' => $data['registration_proof'] ?? '',
        ':price_disclaimer' => $data['price_disclaimer'] ?? '',
        ':condition_type' => $data['type'] ?? ($data['condition_type'] ?? 'NEW'),
        ':features' => $data['features'] ?? '',
        ':date' => $data['date'] ?? '',
        ':engine_number' => $data['engine_number'] ?? '',
        ':chassis_number' => $data['chassis_number'] ?? '',
        ':colors' => processJsonField($data['colors'] ?? ''),
        ':color_images' => isset($data['color_images']) ? json_encode($data['color_images']) : '{}',
        ':custom_fittings' => isset($data['custom_fittings']) ? json_encode($data['custom_fittings']) : '[]',
        ':mandatory_fittings' => isset($data['mandatory_fittings']) ? json_encode($data['mandatory_fittings']) : '[]',
        ':additional_fittings' => isset($data['additional_fittings']) ? json_encode($data['additional_fittings']) : '[]',
        ':front_brake' => $data['front_brake'] ?? '',
        ':rear_brake' => $data['rear_brake'] ?? '',
        ':abs_type' => $data['abs_type'] ?? '',
        ':wheel_type' => $data['wheel_type'] ?? '',
        ':image_paths' => processJsonField($data['image_paths'] ?? ''),
        ':max_torque' => $data['max_torque'] ?? '',
        ':id' => $bikeId
    ];

    try {
        $stmt->execute($params);
        sendResponse("success", "Bike updated successfully");
    } catch (PDOException $e) {
        if (strpos($e->getMessage(), "Unknown column 'max_torque'") !== false) {
            // Fallback for missing column
            $sql = str_replace(", max_torque = :max_torque", "", $sql);
            unset($params[':max_torque']);
            $stmt = $conn->prepare($sql);
            $stmt->execute($params);
            sendResponse("success", "Bike updated successfully (max_torque skipped)");
        } else {
            throw $e;
        }
    }

} catch (Exception $e) {
    http_response_code(400);
    sendResponse("error", $e->getMessage());
}