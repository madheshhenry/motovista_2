<?php
header('Content-Type: application/json; charset=UTF-8');
header('Access-Control-Allow-Origin: *'); 
header('Cache-Control: no-cache, no-store, must-revalidate');
header('Pragma: no-cache');
header('Expires: 0');
header('Cache-Control: no-cache, no-store, must-revalidate');
header('Pragma: no-cache');
header('Expires: 0');
require_once '../config/db_connect.php';

try {
    if (!$conn) {
        die(json_encode(["success" => false, "message" => "Database connection failed"]));
    }

    $customerId = isset($_GET['customer_id']) ? $_GET['customer_id'] : 0;
    if (!$customerId) {
        die(json_encode(["success" => false, "message" => "Missing customer_id"]));
    }

    $sql = "SELECT 
                cr.id as request_id, 
                cr.bike_id,
                cr.bike_name, 
                cr.bike_variant, 
                cr.bike_color as bike_color_name, 
                COALESCE(cr.bike_price, 0) as bike_price,
                cr.created_at as purchase_date,
                cr.status as request_status,
                b.id as physical_bike_id,
                b.engine_number, 
                b.chassis_number,
                b.image_paths as physical_bike_images,
                il.policy_number,
                il.full_insurance_expiry,
                il.status as insurance_status,
                COALESCE(el.total_amount, 0) as emi_total_amount,
                COALESCE(el.paid_amount, 0) as emi_paid_amount,
                COALESCE(el.emi_monthly_amount, 0) as emi_monthly_amount,
                COALESCE(el.duration_months, 0) as emi_duration_months,
                COALESCE(el.status, 'N/A') as emi_status,
                COALESCE(el.remaining_amount, 0) as emi_remaining_amount,
                COALESCE(el.id, 0) as emi_ledger_id
            FROM customer_requests cr
            LEFT JOIN registration_ledger rl ON cr.id = rl.order_id
            LEFT JOIN bikes b ON rl.physical_bike_id = b.id
            LEFT JOIN insurance_ledger il ON cr.id = il.order_id
            LEFT JOIN emi_ledgers el ON cr.id = el.request_id
            WHERE cr.customer_id = :cid 
            AND cr.status IN ('completed', 'delivered')
            GROUP BY cr.id
            ORDER BY cr.created_at DESC";

    $stmt = $conn->prepare($sql);
    $stmt->execute([':cid' => $customerId]);
    $bikes = $stmt->fetchAll(PDO::FETCH_ASSOC);

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
                        if (strcasecmp(trim($colorObj['color_name'] ?? ''), trim($bike['bike_color_name'])) == 0) {
                            $foundHex = $colorObj['hex_code'] ?? '#000000';
                            if (!empty($colorObj['image_paths']) && is_array($colorObj['image_paths'])) $foundVariantImage = $colorObj['image_paths'][0];
                            break;
                        }
                    }
                }
            }
        }

        $bike['bike_color_hex'] = !empty($foundHex) ? $foundHex : '#000000';

        // CLEAN PRICES (Strip ₹ and spaces for Android parsing)
        $bike['bike_price'] = preg_replace('/[^0-9.]/', '', $bike['bike_price']);
        $bike['emi_total_amount'] = preg_replace('/[^0-9.]/', '', $bike['emi_total_amount']);
        $bike['emi_paid_amount'] = preg_replace('/[^0-9.]/', '', $bike['emi_paid_amount']);
        $bike['emi_remaining_amount'] = preg_replace('/[^0-9.]/', '', $bike['emi_remaining_amount']);
        $bike['emi_monthly_amount'] = preg_replace('/[^0-9.]/', '', $bike['emi_monthly_amount']);

        if (!empty($bike['physical_bike_images']) && $bike['physical_bike_images'] != '[]' && $bike['physical_bike_images'] != 'null') {
            $image_str = str_replace(['[', ']', '"', '\\'], '', $bike['physical_bike_images']);
            $imgParts = explode(',', $image_str);
            $bike['bike_image'] = trim($imgParts[0]);
        } else {
            $bike['bike_image'] = $foundVariantImage;
        }

        $bike['purchase_date_formatted'] = !empty($bike['purchase_date']) ? date('d M Y', strtotime($bike['purchase_date'])) : 'N/A';
    }

    echo json_encode(["success" => true, "data" => $bikes], JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>
