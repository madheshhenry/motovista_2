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

    // 2. Fetch Brand from Bike Models
    $brand = "Unknown";
    if ($bikeId) {
        $stmtModel = $conn->prepare("SELECT brand FROM bike_models WHERE id = :mid");
        $stmtModel->execute([':mid' => $bikeId]);
        $modelData = $stmtModel->fetch(PDO::FETCH_ASSOC);
        if ($modelData) {
            $brand = $modelData['brand'];
        }
    }

    // 3. Fetch Image & Hex from Bike Variants
    $imagePaths = "";
    $finalBikeColor = $bikeColorName; // Default to just name

    if ($bikeId && $bikeVariantName) {
        $stmtVar = $conn->prepare("SELECT colors FROM bike_variants WHERE model_id = :mid AND variant_name = :vname");
        $stmtVar->execute([':mid' => $bikeId, ':vname' => $bikeVariantName]);
        $variantData = $stmtVar->fetch(PDO::FETCH_ASSOC);

        if ($variantData && isset($variantData['colors'])) {
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

                        // Found it
                        if (isset($colorObj['image_paths']) && is_array($colorObj['image_paths']) && count($colorObj['image_paths']) > 0) {
                            $imagePaths = implode(",", $colorObj['image_paths']);
                        }

                        // Get Hex
                        $finalBikeColor = $cName . "|" . $hex;

                        break;
                    }
                }

                // Fallback for image (if not found specifically)
                if (empty($imagePaths) && count($colors) > 0) {
                    $firstColor = $colors[0];
                    if (isset($firstColor['image_paths']) && is_array($firstColor['image_paths']) && count($firstColor['image_paths']) > 0) {
                        $imagePaths = implode(",", $firstColor['image_paths']);
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
        "request_id" => $requestData['request_id'],
        "customer_name" => $requestData['customer_name'],
        "customer_phone" => $requestData['customer_phone'],
        "customer_profile" => $requestData['customer_profile'],
        "status" => $requestData['status'],
        "created_at" => $requestData['created_at'],

        "bike_id" => $requestData['bike_id'],
        "brand" => $brand,
        "bike_name" => $requestData['bike_name'],
        "bike_variant" => $requestData['bike_variant'],
        "bike_color" => $finalBikeColor, // Send Name|Hex
        "on_road_price" => $requestData['bike_price'],
        "selected_fittings" => $requestData['selected_fittings'],

        // These fields are expected by OrderSummaryData model but might not be in our snapshot
        // We can send defaults or empty strings to avoid crashing
        "year" => "",
        "engine_cc" => "",
        "fuel_type" => "",
        "mileage" => "",

        "image_paths" => $imagePaths,

        // Stock Check info
        "stock_count" => 0,
        "is_in_stock" => false
    ];

    // 4. Check Real-time Stock in 'bikes' table
    if ($brand && $requestData['bike_name']) {
        // Prepare search terms
        $searchModel = trim($requestData['bike_name']);
        $searchVariant = trim($requestData['bike_variant']);

        // Color matching can be tricky. requestData['bike_color'] might be "Red" or "Red|#FF0000".
        // bikes table 'colors' column might be "Red" or "Red, Black". 
        // We'll try to match strict first, then loose.
        $searchColor = trim($requestData['bike_color']);
        if (strpos($searchColor, '|') !== false) {
            $parts = explode('|', $searchColor);
            $searchColor = trim($parts[0]); // Just "Red"
        }

        // Count query
        // We match model and variant. For color, we use LIKE to handle partial matches or case differences.
        // We ensure condition_type is NEW (assuming requests are for new bikes).
        $stockSql = "SELECT COUNT(*) as count FROM bikes 
                     WHERE (model = :model OR model LIKE :model_like)
                     AND (variant = :variant OR variant LIKE :variant_like)
                     AND colors LIKE :color
                     AND status = 'Available'
                     AND condition_type = 'NEW'";

        $stmtStock = $conn->prepare($stockSql);
        $stmtStock->execute([
            ':model' => $searchModel,
            ':model_like' => '%' . $searchModel . '%',
            ':variant' => $searchVariant,
            ':variant_like' => '%' . $searchVariant . '%',
            ':color' => '%' . $searchColor . '%'
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