<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';
    if (!$authHeader)
        $authHeader = isset($_SERVER['HTTP_AUTHORIZATION']) ? $_SERVER['HTTP_AUTHORIZATION'] : '';

    if (!$authHeader)
        throw new Exception("Authorization token missing");

    $token = str_replace('Bearer ', '', $authHeader);
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);
    if (!$payload)
        throw new Exception("Unauthorized access");

    // MODIFICATION: Inner Join with `brands` table to only fetch models for Brands that exist in the Inventory Brands list
    // This satisfies the user requirement: "inventory screen press panna vara brand mattum enaku add stock screen la varanum"

    $sqlModels = "SELECT bm.id, bm.brand, bm.model_name as model, bm.created_at 
                  FROM bike_models bm
                  INNER JOIN brands b ON bm.brand = b.brand_name 
                  ORDER BY bm.brand ASC, bm.model_name ASC";

    $stmtModels = $conn->query($sqlModels);
    $models = $stmtModels->fetchAll(PDO::FETCH_ASSOC);

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

    echo json_encode([
        "success" => true,
        "data" => $finalData
    ]);

} catch (Exception $e) {
    // Debug logging
    file_put_contents('debug_bikes_error.txt', date('[Y-m-d H:i:s] ') . "get_new_bikes.php: " . $e->getMessage() . "\nHeaders: " . json_encode(apache_request_headers()) . "\n", FILE_APPEND);

    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);
}
?>