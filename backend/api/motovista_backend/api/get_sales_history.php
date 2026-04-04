<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // We join bikes with registration_ledger (to link to order) 
    // and customer_requests (to get price and payment details)
    // We join bikes with registration_ledger (to link to order) 
    // and customer_requests (to get price and payment details)
    $sql = "SELECT 
                b.id as bike_id, 
                b.brand, 
                b.model, 
                b.variant, 
                b.colors as bike_color, 
                b.image_paths,
                b.engine_number, 
                b.chassis_number,
                rl.order_id, 
                rl.customer_name, 
                rl.created_at as sale_date,
                cr.customer_id,
                cr.bike_price as total_value, 
                cr.status as request_status,
                cr.customer_phone,
                CONCAT(u.house_no, ', ', u.street, ', ', u.city, ', ', u.state, ' - ', u.pincode) as customer_address,
                CASE WHEN el.id IS NOT NULL THEN 'EMI' ELSE 'Cash' END as payment_type,
                (SELECT bv.colors FROM bike_variants bv 
                 JOIN bike_models bm ON bv.model_id = bm.id 
                 WHERE (bm.brand = b.brand AND bm.model_name = b.model) 
                 OR (bm.brand = b.brand AND bm.model_name LIKE CONCAT('%', b.model, '%'))
                 OR (bm.brand = b.brand AND b.model LIKE CONCAT('%', bm.model_name, '%'))
                 OR (bm.brand = b.brand AND REPLACE(bm.model_name, ' ', '') = REPLACE(b.model, ' ', ''))
                 LIMIT 1) as catalog_colors
            FROM registration_ledger rl
            LEFT JOIN bikes b ON rl.physical_bike_id = b.id
            JOIN customer_requests cr ON rl.order_id = cr.id
            LEFT JOIN users u ON cr.customer_id = u.id
            LEFT JOIN emi_ledgers el ON cr.id = el.request_id
            WHERE cr.status IN ('completed', 'delivered')
            ORDER BY rl.created_at DESC";

    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $sales = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Format the data for the frontend
    foreach ($sales as &$sale) {
        $sale['bike_id'] = (int)$sale['bike_id'];
        $sale['order_id'] = (int)$sale['order_id'];
        $sale['customer_id'] = (int)$sale['customer_id'];
        
        // Format date for better display
        $date = new DateTime($sale['sale_date']);
        $sale['formatted_date'] = $date->format('M d, Y');
        
        // --- 1. Robust Color & Hex Parsing ---
        $colorName = "N/A";
        $colorHex = "#808080"; // Default gray

        $rawColor = $sale['bike_color'] ?? "";
        $colorData = json_decode($rawColor, true);
        
        if (json_last_error() === JSON_ERROR_NONE && is_array($colorData)) {
            $colorName = $colorData[0] ?? "N/A";
            $colorHex = $colorData[1] ?? "#808080";
        } else if (strpos($rawColor, '|') !== false) {
            $parts = explode('|', $rawColor);
            $colorName = trim($parts[0]);
            $colorHex = trim($parts[1] ?? "#808080");
        } else {
            $colorName = $rawColor ?: "N/A";
            // Check if name itself contains | (fallback for mixed up data)
            if (strpos($colorName, '|') !== false) {
                $parts = explode('|', $colorName);
                $colorName = trim($parts[0]);
                $colorHex = trim($parts[1] ?? "#808080");
            }
        }
        
        $sale['bike_color_name'] = $colorName;
        $sale['bike_color_hex'] = $colorHex;

        // --- 2. Image Parsing with Catalog Fallback ---
        $bikeImage = null;
        
        // Try Inventory images first
        $images = json_decode($sale['image_paths'] ?? '', true);
        if (is_array($images) && count($images) > 0) {
            $bikeImage = $images[0];
        }
        
        // Fallback to Catalog if inventory image is missing
        if (empty($bikeImage) && !empty($sale['catalog_colors'])) {
            $catColors = json_decode($sale['catalog_colors'], true);
            if (is_array($catColors) && !empty($catColors)) {
                foreach ($catColors as $c) {
                    if (!empty($c['image_paths']) && is_array($c['image_paths'])) {
                        $bikeImage = $c['image_paths'][0];
                        break;
                    }
                }
            }
        }
        
        $sale['bike_image'] = $bikeImage;
    }

    echo json_encode(["success" => true, "data" => $sales]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>
