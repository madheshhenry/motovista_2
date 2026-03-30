<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    // 1. Authorization
    $allHeaders = array_change_key_case(apache_request_headers(), CASE_LOWER);
    $authHeader = $allHeaders['authorization'] ?? '';

    if (!$authHeader) {
        $authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? '';
    }

    if (!$authHeader) {
        throw new Exception("Authorization token missing");
    }

    $token = str_replace('Bearer ', '', $authHeader);
    $payload = (new JWT_HELPER())->validateToken($token);
    if (!$payload)
        throw new Exception("Unauthorized access");

    // 2. Fetch Models
    $sql = "SELECT id, brand, model_name, model_year, engine_cc, max_power, max_torque, created_at FROM bike_models ORDER BY created_at DESC";
    $stmt = $conn->query($sql);
    $models = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $finalList = [];

    // 3. For each model, fetch variants aggregation
    $stmtVariants = $conn->prepare("SELECT variant_name, colors, price_details FROM bike_variants WHERE model_id = :mid");

    foreach ($models as $model) {
        $stmtVariants->execute([':mid' => $model['id']]);
        $variants = $stmtVariants->fetchAll(PDO::FETCH_ASSOC);

        $variantCount = count($variants);
        $variantNames = [];
        $thumbnail = null;
        $priceStart = "N/A";

        foreach ($variants as $v) {
            $variantNames[] = $v['variant_name'];

            // 1. Try to find a thumbnail (First image of first color)
            if ($thumbnail === null && !empty($v['colors'])) {
                $colors = json_decode($v['colors'], true);
                if (json_last_error() === JSON_ERROR_NONE && is_array($colors) && !empty($colors)) {
                    foreach ($colors as $c) {
                        if (!empty($c['image_paths']) && is_array($c['image_paths']) && !empty($c['image_paths'])) {
                            $thumbnail = $c['image_paths'][0];
                            break; // Found one
                        }
                    }
                }
            }

            // 2. Try to find a price (Ex-Showroom)
            // Only update if we haven't found a price yet
            if ($priceStart === "N/A") {
                $prices = json_decode($v['price_details'], true);
                if (json_last_error() === JSON_ERROR_NONE && isset($prices['ex_showroom'])) {
                    $priceStart = $prices['ex_showroom'];
                }
            }
        }

        // Map to Android Model Expectations
        // The Android App expects: brand, model, year, image_url (thumbnail), price (display), variant (string summary)
        $bike = [
            'id' => $model['id'],
            'brand' => $model['brand'],
            'model' => $model['model_name'],
            'year' => $model['model_year'],
            'price' => $priceStart,
            'type' => 'NEW', // It's from this table so it's NEW
            'image_url' => $thumbnail,
            // OLD Fields mapped
            'variant' => $variantCount > 0 ? "$variantCount Variants: " . implode(", ", array_slice($variantNames, 0, 3)) . (count($variantNames) > 3 ? "..." : "") : "Standard",
            'date' => $model['created_at'],
            // Add raw variant info just in case
            'variant_count' => $variantCount,
            'engine_cc' => $model['engine_cc'],
            'max_power' => $model['max_power'],
            'max_torque' => $model['max_torque']
        ];

        $finalList[] = $bike;
    }

    echo json_encode([
        "success" => true,
        "status" => "success",
        "data" => $finalList
    ]);

} catch (Exception $e) {
    // Debug logging
    file_put_contents('debug_bikes_error.txt', date('[Y-m-d H:i:s] ') . "get_bikes.php: " . $e->getMessage() . "\nHeaders: " . json_encode(apache_request_headers()) . "\n", FILE_APPEND);

    http_response_code(400); // Bad Request
    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);
}
?>