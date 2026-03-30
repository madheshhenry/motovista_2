<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
        throw new Exception("Method Not Allowed");
    }

    if (!isset($_GET['customer_id'])) {
        throw new Exception("Missing customer_id");
    }

    $customerId = $_GET['customer_id'];

    // 1. Fetch COMPLETED orders
    $sql = "SELECT 
                cr.id as request_id, 
                cr.bike_id,
                cr.bike_name, 
                cr.bike_variant, 
                cr.bike_color, 
                cr.bike_price, 
                cr.created_at as purchase_date,
                cr.status as request_status,
                b.id as physical_bike_id,
                b.engine_number, 
                b.chassis_number,
                b.image_paths as physical_bike_images,
                il.policy_number,
                il.full_insurance_expiry,
                il.third_party_expiry,
                il.status as insurance_status,
                el.total_amount as emi_total_amount,
                el.paid_amount as emi_paid_amount,
                el.emi_monthly_amount,
                el.duration_months as emi_duration_months,
                el.status as emi_status,
                el.remaining_amount as emi_remaining_amount,
                el.id as emi_ledger_id
            FROM customer_requests cr
            LEFT JOIN registration_ledger rl ON cr.id = rl.order_id
            LEFT JOIN bikes b ON (
                (rl.physical_bike_id IS NOT NULL AND b.id = rl.physical_bike_id)
                OR
                (rl.physical_bike_id IS NULL AND b.customer_name = cr.customer_name AND b.model = cr.bike_name AND b.status = 'Sold')
            )
            LEFT JOIN insurance_ledger il ON cr.id = il.order_id
            LEFT JOIN emi_ledgers el ON cr.id = el.request_id
            WHERE cr.customer_id = :cid 
            AND cr.status IN ('completed', 'delivered')
            GROUP BY cr.id
            ORDER BY cr.created_at DESC";

    $stmt = $conn->prepare($sql);
    $stmt->execute([':cid' => $customerId]);
    $bikes = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 2. Process data with fallback to bike_variants
    foreach ($bikes as &$bike) {
        $foundHex = "";
        $foundVariantImage = "";

        if (!empty($bike['bike_id']) && !empty($bike['bike_variant'])) {
            $stmtVar = $conn->prepare("SELECT colors FROM bike_variants WHERE model_id = :mid AND variant_name = :vname");
            $stmtVar->execute([':mid' => $bike['bike_id'], ':vname' => $bike['bike_variant']]);
            $variantData = $stmtVar->fetch(PDO::FETCH_ASSOC);

            if ($variantData && !empty($variantData['colors'])) {
                $colors = json_decode($variantData['colors'], true);
                if (is_array($colors)) {
                    foreach ($colors as $colorObj) {
                        $cName = $colorObj['color_name'] ?? '';
                        $hex = $colorObj['hex_code'] ?? ($colorObj['color_hex'] ?? '#000000');

                        // Compare ignoring case and spaces
                        if (strcasecmp(trim($cName), trim($bike['bike_color'])) == 0) {
                            $foundHex = $hex;
                            if (!empty($colorObj['image_paths']) && is_array($colorObj['image_paths'])) {
                                $foundVariantImage = $colorObj['image_paths'][0];
                            }
                            break;
                        }
                    }
                }
            }
        }

        // Finalize Color
        $bike['bike_color_name'] = $bike['bike_color'];
        $bike['bike_color_hex'] = !empty($foundHex) ? $foundHex : '#000000';

        // Finalize Image
        if (!empty($bike['physical_bike_images']) && $bike['physical_bike_images'] != '[]') {
            $imgParts = explode(',', $bike['physical_bike_images']);
            $bike['bike_image'] = trim($imgParts[0]);
        } else {
            $bike['bike_image'] = $foundVariantImage;
        }

        // Format Date
        $bike['purchase_date_formatted'] = date('d M Y', strtotime($bike['purchase_date']));
    }

    echo json_encode([
        "success" => true,
        "data" => $bikes
    ]);

} catch (Exception $e) {
    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);
}
?>