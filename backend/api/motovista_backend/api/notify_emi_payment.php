<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $data = json_decode(file_get_contents("php://input"), true);

    if (!isset($data['ledger_id']) || !isset($data['amount'])) {
        throw new Exception("Missing required fields");
    }

    $ledgerId = $data['ledger_id'];
    $amount = floatval($data['amount']);
    $date = date('Y-m-d');
    $mode = 'UPI/Showroom'; // Default for notification
    $remarks = isset($data['remarks']) ? $data['remarks'] : 'Customer notified of payment';

    if ($amount <= 0) {
        throw new Exception("Invalid amount");
    }

    // Insert as PENDING payment
    $stmtIns = $conn->prepare("INSERT INTO emi_payments (ledger_id, amount_paid, payment_date, payment_mode, remarks, status) VALUES (:lid, :amt, :dt, :mode, :rem, 'pending')");
    $stmtIns->execute([
        ':lid' => $ledgerId,
        ':amt' => $amount,
        ':dt' => $date,
        ':mode' => $mode,
        ':rem' => $remarks
    ]);

    echo json_encode(["success" => true, "message" => "Admin notified. Verification in progress."]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>