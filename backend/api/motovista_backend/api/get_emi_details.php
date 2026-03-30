<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    if (!isset($_GET['ledger_id'])) {
        throw new Exception("Ledger ID is required");
    }
    $ledgerId = intval($_GET['ledger_id']);

    // 1. Fetch Ledger + Customer Info + Bike Info
    $sql = "SELECT el.*, 
                   cr.customer_name, cr.customer_phone, cr.customer_profile,
                   cr.bike_name as vehicle_name, 
                   cr.bike_variant as bike_variant,
                   cr.bike_color as bike_color,
                   cr.bike_id as model_id,
                   b.brand as bike_brand,
                   b.model as bike_model,
                   b.image_paths as bike_images,
                   b.engine_number,
                   b.chassis_number,
                   b.on_road_price
            FROM emi_ledgers el
            LEFT JOIN customer_requests cr ON el.request_id = cr.id
            LEFT JOIN customers c ON cr.customer_id = c.id
            LEFT JOIN bikes b ON cr.bike_id = b.id OR (cr.bike_name = b.model AND cr.bike_variant = b.variant AND cr.bike_color = b.color)
            WHERE el.id = :lid";

    $stmt = $conn->prepare($sql);
    $stmt->execute([':lid' => $ledgerId]);
    $ledger = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$ledger) {
        throw new Exception("Ledger not found");
    }

    // 2. Fetch Payments
    $sqlPay = "SELECT * FROM emi_payments WHERE ledger_id = :lid ORDER BY payment_date ASC";
    $stmtPay = $conn->prepare($sqlPay);
    $stmtPay->execute([':lid' => $ledgerId]);
    $payments = $stmtPay->fetchAll(PDO::FETCH_ASSOC);

    // 3. Format Data
    $ledger['total_amount'] = number_format($ledger['total_amount'], 2, '.', '');
    $ledger['paid_amount'] = number_format($ledger['paid_amount'], 2, '.', '');
    $ledger['remaining_amount'] = number_format($ledger['remaining_amount'], 2, '.', '');
    $ledger['emi_monthly_amount'] = number_format($ledger['emi_monthly_amount'], 2, '.', '');

    // Fallbacks
    if (empty($ledger['bike_model']))
        $ledger['bike_model'] = $ledger['vehicle_name'];
    if (empty($ledger['on_road_price']))
        $ledger['on_road_price'] = $ledger['total_amount'];

    $verifiedPayments = [];
    $pendingPayments = [];
    foreach ($payments as &$pay) {
        $pay['amount_paid'] = number_format($pay['amount_paid'], 2, '.', '');
        if ($pay['status'] == 'verified') {
            $verifiedPayments[] = $pay;
        } elseif ($pay['status'] == 'pending') {
            $pendingPayments[] = $pay;
        }
    }

    // 3.5. Image Fallback Logic
    // If no physical bike image, lookup variant image
    if (empty($ledger['bike_images']) || $ledger['bike_images'] == '[]') {
        $foundVariantImage = null;
        if (!empty($ledger['model_id']) && !empty($ledger['bike_variant'])) {
            $stmtVar = $conn->prepare("SELECT colors FROM bike_variants WHERE model_id = :mid AND variant_name = :vname");
            $stmtVar->execute([':mid' => $ledger['model_id'], ':vname' => $ledger['bike_variant']]);
            $variantData = $stmtVar->fetch(PDO::FETCH_ASSOC);

            if ($variantData && !empty($variantData['colors'])) {
                $colors = json_decode($variantData['colors'], true);
                if (is_array($colors)) {
                    foreach ($colors as $colorObj) {
                        $cName = $colorObj['color_name'] ?? '';
                        // Compare ignoring case and spaces
                        if (strcasecmp(trim($cName), trim($ledger['bike_color'])) == 0) {
                            if (!empty($colorObj['image_paths']) && is_array($colorObj['image_paths'])) {
                                $foundVariantImage = $colorObj['image_paths'][0];
                            }
                            break;
                        }
                    }
                }
            }
        }
        if ($foundVariantImage) {
            $ledger['bike_images'] = $foundVariantImage;
        }
    }

    // 4. Generate Payment Schedule
    $paymentSchedule = [];
    $startDate = !empty($ledger['start_date']) ? $ledger['start_date'] : substr($ledger['created_at'], 0, 10);
    $duration = (int) $ledger['duration_months'];
    $emiAmount = $ledger['emi_monthly_amount'];

    $verifiedCount = count($verifiedPayments);
    $currentDate = date('Y-m-d');

    $isNextPendingFound = false;

    for ($i = 1; $i <= $duration; $i++) {
        $dueDate = date('Y-m-d', strtotime($startDate . " + $i month"));
        $item = [
            "installment_no" => $i,
            "due_date" => $dueDate,
            "amount" => $emiAmount,
            "status" => "upcoming",
            "payment_date" => null,
            "amount_paid" => "0.00",
            "fine" => 0,
            "payment_id" => null // To help admin identify which payment to verify
        ];

        if ($i <= $verifiedCount) {
            $item['status'] = 'paid';
            $item['payment_date'] = $verifiedPayments[$i - 1]['payment_date'];
            $item['amount_paid'] = $verifiedPayments[$i - 1]['amount_paid'];
        } else {
            // Check if there's a pending payment for this NEXT installment
            if (!$isNextPendingFound && count($pendingPayments) > 0) {
                // Peek at the first pending payment
                $pending = array_shift($pendingPayments);
                $item['status'] = 'reviewing';
                $item['payment_date'] = $pending['payment_date'];
                $item['amount_paid'] = $pending['amount_paid'];
                $item['payment_id'] = $pending['id'];
                $isNextPendingFound = true;
            } else {
                if ($dueDate < $currentDate) {
                    $item['status'] = 'overdue';
                    $daysOverdue = (strtotime($currentDate) - strtotime($dueDate)) / (60 * 60 * 24);
                    $item['fine'] = max(0, floor($daysOverdue) * 50);
                } elseif (!$isNextPendingFound) {
                    $item['status'] = 'pending';
                    $isNextPendingFound = true; // Mark that we found the "current" pending one (unpaid)
                }
            }
        }
        $paymentSchedule[] = $item;
    }

    echo json_encode([
        "success" => true,
        "data" => [
            "ledger" => $ledger,
            "payments" => $payments,
            "payment_schedule" => $paymentSchedule
        ]
    ]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>