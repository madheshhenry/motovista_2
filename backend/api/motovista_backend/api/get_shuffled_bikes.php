<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    // Fetch all available bikes (models) that have at least one variant
    // We'll join bike_models, bike_variants to get a good mix
    $sql = "SELECT 
                bm.id as model_id, 
                bm.brand, 
                bm.model_name as model,
                bv.variant_name,
                bv.colors,
                bv.price_details
            FROM bike_models bm
            INNER JOIN bike_variants bv ON bm.id = bv.model_id
            WHERE 1";

    $stmt = $conn->query($sql);
    $allBikesRaw = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $shuffledList = [];

    foreach ($allBikesRaw as $row) {
        $colors = json_decode($row['colors'], true);
        if (is_array($colors)) {
            foreach ($colors as $colorObj) {
                // Flatten into individual bike options (Model + Variant + Color)
                $bike = [
                    "id" => $row['model_id'],
                    "brand" => $row['brand'],
                    "model" => $row['model'],
                    "type" => "NEW",
                    "variant" => $row['variant_name'],
                    "color_name" => $colorObj['color_name'] ?? 'Unknown',
                    "color_hex" => $colorObj['hex_code'] ?? ($colorObj['color_hex'] ?? '#000000'),
                    "on_road_price" => "0",
                    "image" => ""
                ];

                // Get price
                $priceDetails = json_decode($row['price_details'], true);
                if (isset($priceDetails['total_on_road'])) {
                    $bike['on_road_price'] = $priceDetails['total_on_road'];
                }

                // Get first image path
                if (isset($colorObj['image_paths']) && is_array($colorObj['image_paths']) && count($colorObj['image_paths']) > 0) {
                    $bike['image'] = $colorObj['image_paths'][0];
                }

                $shuffledList[] = $bike;
            }
        }
    }

    // Shuffle the list locally in PHP
    shuffle($shuffledList);

    // Limit to 30 items for the home screen (ensure enough for brand rows)
    $finalList = array_slice($shuffledList, 0, 30);

    echo json_encode([
        "success" => true,
        "data" => $finalList
    ]);

} catch (Exception $e) {
    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);
}
?>