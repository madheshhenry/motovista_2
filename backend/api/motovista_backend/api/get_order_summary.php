<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
        throw new Exception("Method Not Allowed");
    }

    if (!isset($_GET['request_id'])) {
        throw new Exception("Missing request_id");
    }

    $requestId = $_GET['request_id'];

    // 1. Fetch Request & Customer Details
    $sql = "SELECT 
                cr.id as request_id, 
                cr.customer_name, 
                cr.customer_phone, 
                cr.status, 
                cr.bike_id, 
                cr.bike_name, 
                cr.bike_variant, 
                cr.bike_color, 
                cr.bike_price, 
                cr.selected_fittings,
                cr.created_at,
                c.profile_image as customer_profile
            FROM customer_requests cr
            LEFT JOIN customers c ON cr.customer_id = c.id
            WHERE cr.id = :id";

    $stmt = $conn->prepare($sql);
    $stmt->execute([':id' => $requestId]);
    $requestData = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$requestData) {
        throw new Exception("Request not found");
    }

    $bikeId = $requestData['bike_id'];
    $bikeVariantName = $requestData['bike_variant'];
    $bikeColorName = $requestData['bike_color'];

    // 2. Fetch Brand and Exact Model Name from Bike Models (Canonical Names)
    $brand = "Unknown";
    $canonicalModel = "";
    if ($bikeId) {
        $stmtModel = $conn->prepare("SELECT brand, model_name FROM bike_models WHERE id = :mid");
        $stmtModel->execute([':mid' => $bikeId]);
        $modelData = $stmtModel->fetch(PDO::FETCH_ASSOC);
        if ($modelData) {
            $brand = $modelData['brand'];
            $canonicalModel = $modelData['model_name'];
        }
    }

    // 3. Fetch Image & Hex from Bike Variants
    $imagePaths = "";
    $finalBikeColor = $bikeColorName; // Default to just name

    if ($bikeId && $bikeVariantName) {
        $stmtVar = $conn->prepare("SELECT variant_name, colors FROM bike_variants WHERE model_id = :mid AND LOWER(TRIM(variant_name)) = LOWER(TRIM(:vname))");
        $stmtVar->execute([':mid' => $bikeId, ':vname' => $bikeVariantName]);
        $variantData = $stmtVar->fetch(PDO::FETCH_ASSOC);

        if ($variantData) {
            // Case correction if needed, but we mainly need colors
            if (isset($variantData['colors'])) {
                $colors = json_decode($variantData['colors'], true);
                if (is_array($colors)) {
                    // Find matching color
                    foreach ($colors as $colorObj) {
                        $cName = $colorObj['color_name'] ?? '';
                        $hex = $colorObj['hex_code'] ?? ($colorObj['color_hex'] ?? '#000000');

                        // Match by Name OR Hex
                        if (
                            strcasecmp(trim($cName), trim($bikeColorName)) == 0 ||
                            strcasecmp(trim($hex), trim($bikeColorName)) == 0
                        ) {
                            if (isset($colorObj['image_paths']) && is_array($colorObj['image_paths']) && count($colorObj['image_paths']) > 0) {
                                $imagePaths = implode(",", $colorObj['image_paths']);
                            }
                            $finalBikeColor = $cName . "|" . $hex;
                            break;
                        }
                    }
                }
            }
        }
    }

    // --- Fetch Registration Progress ---
    $ledgerSql = "SELECT step_1_status, step_2_status, step_3_status, step_4_status FROM registration_ledger WHERE order_id = :id";
    $ledgerStmt = $conn->prepare($ledgerSql);
    $ledgerStmt->execute([':id' => $requestId]);
    $ledgerData = $ledgerStmt->fetch(PDO::FETCH_ASSOC);

    // Construct Response Data
    $responseData = [
        "registration_progress" => $ledgerData ? $ledgerData : null,
        "request_id" => (int) $requestData['request_id'],
        "customer_name" => $requestData['customer_name'],
        "customer_phone" => $requestData['customer_phone'],
        "customer_profile" => $requestData['customer_profile'],
        "status" => $requestData['status'],
        "created_at" => $requestData['created_at'],

        "bike_id" => (int) $requestData['bike_id'],
        "brand" => $brand,
        "bike_name" => $requestData['bike_name'],
        "bike_variant" => $requestData['bike_variant'],
        "bike_color" => $finalBikeColor, // Send Name|Hex
        "on_road_price" => $requestData['bike_price'],
        "selected_fittings" => $requestData['selected_fittings'],

        "year" => "",
        "engine_cc" => "",
        "fuel_type" => "",
        "mileage" => "",
        "image_paths" => $imagePaths,

        // Stock Check info
        "stock_count" => 0,
        "is_in_stock" => false
    ];

    // 4. Check Real-time Stock in 'bikes' table using Canonical Names
    // We use the exact model/brand from bike_models table for better accuracy.
    if ($brand && ($canonicalModel || $requestData['bike_name'])) {
        $searchModel = !empty($canonicalModel) ? $canonicalModel : $requestData['bike_name'];
        $searchVariant = trim($requestData['bike_variant']);
        $searchColor = trim($requestData['bike_color']);

        if (strpos($searchColor, '|') !== false) {
            $searchColor = trim(explode('|', $searchColor)[0]);
        }

        // Count query - Robust matching
        $stockSql = "SELECT COUNT(*) as count FROM bikes 
                     WHERE (LOWER(TRIM(brand)) = LOWER(TRIM(:brand)))
                     AND (LOWER(TRIM(model)) = LOWER(TRIM(:model)) OR model LIKE :model_like)
                     AND (LOWER(TRIM(variant)) = LOWER(TRIM(:variant)) OR variant LIKE :variant_like)
                     AND (colors LIKE :color OR LOWER(TRIM(colors)) = LOWER(TRIM(:color_exact)))
                     AND (status = 'Available' OR status IS NULL)
                     AND condition_type = 'NEW'";

        $stmtStock = $conn->prepare($stockSql);
        $stmtStock->execute([
            ':brand' => $brand,
            ':model' => $searchModel,
            ':model_like' => '%' . $searchModel . '%',
            ':variant' => $searchVariant,
            ':variant_like' => '%' . $searchVariant . '%',
            ':color' => '%' . $searchColor . '%',
            ':color_exact' => $searchColor
        ]);

        $stockResult = $stmtStock->fetch(PDO::FETCH_ASSOC);
        $count = $stockResult ? (int) $stockResult['count'] : 0;

        $responseData['stock_count'] = $count;
        $responseData['is_in_stock'] = ($count > 0);
    }

    echo json_encode(["success" => true, "data" => $responseData]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>