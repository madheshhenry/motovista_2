<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

ini_set('display_errors', 0);
error_reporting(E_ALL);

file_put_contents(__DIR__ . '/REACHED_UNIFIED.txt', date('[Y-m-d H:i:s] ') . "Unified call reached from IP: " . ($_SERVER['REMOTE_ADDR'] ?? 'UNKNOWN') . "\n", FILE_APPEND);

try {
    // 1. Verify Token
    $authHeader = '';
    if (isset($_SERVER['HTTP_AUTHORIZATION'])) {
        $authHeader = $_SERVER['HTTP_AUTHORIZATION'];
    } elseif (isset($_SERVER['REDIRECT_HTTP_AUTHORIZATION'])) {
        $authHeader = $_SERVER['REDIRECT_HTTP_AUTHORIZATION'];
    } elseif (function_exists('getallheaders')) {
        $headers = array_change_key_case(getallheaders(), CASE_LOWER);
        $authHeader = $headers['authorization'] ?? '';
    }

    if (!$authHeader) throw new Exception("Authorization token missing");

    $token = str_replace('Bearer ', '', $authHeader);
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);
    if (!$payload) throw new Exception("Unauthorized access");

    // 2. Fetch Brands (from inventory brands list)
    $stmtBrands = $conn->query("SELECT id, TRIM(brand_name) as brand_name, brand_logo FROM brands ORDER BY brand_name ASC");
    $brandsRaw = $stmtBrands->fetchAll(PDO::FETCH_ASSOC);
    $brandsData = [];
    foreach ($brandsRaw as $b) {
        $brandsData[] = [
            'id' => $b['id'],
            'brand' => $b['brand_name'],
            'logo' => $b['brand_logo']
        ];
    }

    // 3. Fetch Master Catalog Models
    $sqlModels = "SELECT id, TRIM(brand) as brand, TRIM(model_name) as model, created_at FROM bike_models ORDER BY brand ASC, model_name ASC";
    $stmtModels = $conn->query($sqlModels);
    $modelsRaw = $stmtModels->fetchAll(PDO::FETCH_ASSOC);
    
    $catalogData = [];
    $stmtVariants = $conn->prepare("SELECT variant_name, colors FROM bike_variants WHERE model_id = :mid");

    foreach ($modelsRaw as $row) {
        $modelId = $row['id'];
        $stmtVariants->execute([':mid' => $modelId]);
        $variantsRaw = $stmtVariants->fetchAll(PDO::FETCH_ASSOC);

        $variantsList = [];
        $allColorsStrings = [];

        foreach ($variantsRaw as $v) {
            $decodedColors = json_decode($v['colors'], true);
            $vColors = [];
            if (is_array($decodedColors)) {
                foreach ($decodedColors as $c) {
                    $cName = $c['color_name'] ?? 'Unknown';
                    $cHex = $c['hex_code'] ?? ($c['color_hex'] ?? '#CCCCCC');
                    $vColors[] = ["color_name" => $cName, "color_hex" => $cHex];
                    $allColorsStrings[] = $cName . "|" . $cHex;
                }
            }
            $variantsList[] = [
                "variant_name" => $v['variant_name'],
                "colors" => $vColors
            ];
        }

        $catalogData[] = [
            "id" => $row['id'],
            "brand" => $row['brand'],
            "model" => $row['model'],
            "variants" => $variantsList,
            "colors" => array_values(array_unique($allColorsStrings)),
            "date" => $row['created_at']
        ];
    }

    echo json_encode([
        "success" => true,
        "status" => "success",
        "brands" => $brandsData,
        "catalog" => $catalogData
    ]);

} catch (Exception $e) {
    http_response_code(200); // 200 to ensure Android receives the JSON error message
    echo json_encode([
        "success" => false,
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}
?>
