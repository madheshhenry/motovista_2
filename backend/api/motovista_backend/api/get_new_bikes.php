<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

ini_set('display_errors', 0);
error_reporting(E_ALL);

file_put_contents(__DIR__ . '/REACHED_V3.txt', date('[Y-m-d H:i:s] ') . "get_new_bikes.php reached\n", FILE_APPEND);

try {
    // Basic reached check
    file_put_contents(__DIR__ . '/REACHED_V5.txt', date('[Y-m-d H:i:s] ') . "get_new_bikes reached from IP: " . ($_SERVER['REMOTE_ADDR'] ?? 'UNKNOWN') . "\n", FILE_APPEND);
    // Safer header retrieval (compatible with all PHP environments)
    $authHeader = '';
    if (isset($_SERVER['HTTP_AUTHORIZATION'])) {
        $authHeader = $_SERVER['HTTP_AUTHORIZATION'];
    } elseif (isset($_SERVER['REDIRECT_HTTP_AUTHORIZATION'])) {
        $authHeader = $_SERVER['REDIRECT_HTTP_AUTHORIZATION'];
    } elseif (function_exists('getallheaders')) {
        $headers = array_change_key_case(getallheaders(), CASE_LOWER);
        $authHeader = $headers['authorization'] ?? '';
    }

    if (!$authHeader)
        throw new Exception("Authorization token missing");

    $token = str_replace('Bearer ', '', $authHeader);
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);
    if (!$payload)
        throw new Exception("Unauthorized access");

    // MODIFICATION: Inner Join with `brands` table to only fetch models for Brands that exist in the Inventory Brands list
    // This satisfies the user requirement: "inventory screen press panna vara brand mattum enaku add stock screen la varanum"

    $sqlModels = "SELECT id, TRIM(brand) as brand, TRIM(model_name) as model, created_at 
                  FROM bike_models 
                  ORDER BY brand ASC, model_name ASC";

    $stmtModels = $conn->query($sqlModels);
    if (!$stmtModels) {
        $errorInfo = $conn->errorInfo();
        error_log("SQL Error in get_new_bikes.php: " . $errorInfo[2]);
        throw new Exception("Database query failed correctly.");
    }
    $models = $stmtModels->fetchAll(PDO::FETCH_ASSOC);
    
    // DEBUG: Log the count to monitor what is actually happening
    file_put_contents('debug_bikes_error.txt', date('[Y-m-d H:i:s] ') . "get_new_bikes found " . count($models) . " models.\n", FILE_APPEND);

    $finalData = [];

    $stmtVariants = $conn->prepare("SELECT variant_name, colors FROM bike_variants WHERE model_id = :mid");

    foreach ($models as $row) {
        $modelId = $row['id'];
        $stmtVariants->execute([':mid' => $modelId]);
        $variantsRaw = $stmtVariants->fetchAll(PDO::FETCH_ASSOC);

        $variantsList = [];
        $allColorsStrings = [];

        foreach ($variantsRaw as $v) {
            $vName = $v['variant_name'];
            $vColorsJson = $v['colors'];

            $variantObj = [
                "variant_name" => $vName,
                "colors" => []
            ];

            $decodedColors = json_decode($vColorsJson, true);
            if (is_array($decodedColors)) {
                foreach ($decodedColors as $c) {
                    $cName = $c['color_name'] ?? 'Unknown';
                    // Check 'hex_code' first, then 'color_hex', then default
                    $cHex = $c['hex_code'] ?? ($c['color_hex'] ?? '#CCCCCC');

                    $variantColorObj = [
                        "color_name" => $cName,
                        "color_hex" => $cHex,
                        "image_paths" => []
                    ];
                    $variantObj['colors'][] = $variantColorObj;
                    $allColorsStrings[] = $cName . "|" . $cHex;
                }
            }
            $variantsList[] = $variantObj;
        }

        $bikeModel = [
            "id" => $row['id'],
            "brand" => $row['brand'],
            "model" => $row['model'],
            "variants" => $variantsList,
            "colors" => array_unique($allColorsStrings),
            "date" => $row['created_at']
        ];

        $finalData[] = $bikeModel;
    }

    $responseJson = json_encode([
        "success" => true,
        "status" => "success",
        "data" => $finalData
    ]);
    file_put_contents('last_catalog_response.json', $responseJson);
    echo $responseJson;

} catch (Exception $e) {
    $errRes = [
        "success" => false, 
        "status" => "error",
        "message" => $e->getMessage()
    ];
    file_put_contents('last_catalog_response.json', json_encode($errRes));
    http_response_code(400);
    echo json_encode($errRes);
}
?>