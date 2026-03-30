<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $sql = "SELECT ep.*, el.id as ledger_id, 
                   cr.customer_name, cr.bike_name, cr.customer_phone,
                   cr.bike_id as model_id, cr.bike_variant, cr.bike_color,
                   b.image_paths as physical_bike_image
            FROM emi_payments ep
            JOIN emi_ledgers el ON ep.ledger_id = el.id
            JOIN customer_requests cr ON el.request_id = cr.id
            LEFT JOIN bikes b ON cr.bike_id = b.id OR (cr.bike_name = b.model AND cr.bike_variant = b.variant AND cr.bike_color = b.color)
            WHERE ep.status = 'pending'
            ORDER BY ep.created_at DESC";

    $stmt = $conn->query($sql);
    $verifications = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Image Fallback Logic
    foreach ($verifications as &$item) {
        $finalImage = null;

        // 1. Try physical bike image
        if (!empty($item['physical_bike_image']) && $item['physical_bike_image'] != '[]') {
            $imgParts = explode(',', $item['physical_bike_image']);
            $finalImage = trim($imgParts[0]);
        }

        // 2. Fallback to variant image
        if (empty($finalImage)) {
            if (!empty($item['model_id']) && !empty($item['bike_variant'])) {
                $stmtVar = $conn->prepare("SELECT colors FROM bike_variants WHERE model_id = :mid AND variant_name = :vname");
                $stmtVar->execute([':mid' => $item['model_id'], ':vname' => $item['bike_variant']]);
                $variantData = $stmtVar->fetch(PDO::FETCH_ASSOC);

                if ($variantData && !empty($variantData['colors'])) {
                    $colors = json_decode($variantData['colors'], true);
                    if (is_array($colors)) {
                        foreach ($colors as $colorObj) {
                            $cName = $colorObj['color_name'] ?? '';
                            if (strcasecmp(trim($cName), trim($item['bike_color'])) == 0) {
                                if (!empty($colorObj['image_paths']) && is_array($colorObj['image_paths'])) {
                                    $finalImage = $colorObj['image_paths'][0];
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        $item['bike_image'] = $finalImage;
        unset($item['physical_bike_image']); // Cleanup
    }

    echo json_encode([
        "success" => true,
        "data" => $verifications
    ]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["status" => false, "message" => $e->getMessage()]);
}
?>