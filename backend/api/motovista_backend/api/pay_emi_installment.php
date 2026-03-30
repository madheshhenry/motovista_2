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
    $finePaid = isset($data['fine_paid']) ? floatval($data['fine_paid']) : 0.00;
    $date = isset($data['date']) ? $data['date'] : date('Y-m-d');
    $mode = isset($data['mode']) ? $data['mode'] : 'Cash';
    $paymentId = isset($data['payment_id']) ? $data['payment_id'] : null;

    if ($amount < 0) {
        throw new Exception("Invalid amount");
    }

    $conn->beginTransaction();

    if ($paymentId) {
        // Approve existing pending payment
        $stmtUpdPay = $conn->prepare("UPDATE emi_payments SET amount_paid = :amt, fine_paid = :fine, payment_date = :dt, payment_mode = :mode, remarks = :rem, status = 'verified' WHERE id = :pid");
        $stmtUpdPay->execute([
            ':amt' => $amount,
            ':fine' => $finePaid,
            ':dt' => $date,
            ':mode' => $mode,
            ':rem' => $remarks,
            ':pid' => $paymentId
        ]);
    } else {
        // 1. Insert New Verified Payment
        $stmtIns = $conn->prepare("INSERT INTO emi_payments (ledger_id, amount_paid, fine_paid, payment_date, payment_mode, remarks, status) VALUES (:lid, :amt, :fine, :dt, :mode, :rem, 'verified')");
        $stmtIns->execute([
            ':lid' => $ledgerId,
            ':amt' => $amount,
            ':fine' => $finePaid,
            ':dt' => $date,
            ':mode' => $mode,
            ':rem' => $remarks
        ]);
    }

    // 2. Fetch current ledger stats
    $stmtGet = $conn->prepare("SELECT total_amount, paid_amount, emi_monthly_amount, next_due_date FROM emi_ledgers WHERE id = :lid FOR UPDATE");
    $stmtGet->execute([':lid' => $ledgerId]);
    $ledger = $stmtGet->fetch(PDO::FETCH_ASSOC);

    if (!$ledger) {
        $conn->rollBack();
        throw new Exception("Ledger not found");
    }

    $newPaidAmount = floatval($ledger['paid_amount']) + $amount;
    $totalAmount = floatval($ledger['total_amount']);
    $monthlyEmi = floatval($ledger['emi_monthly_amount']);

    $status = ($newPaidAmount >= $totalAmount) ? 'completed' : 'active';

    // Update Next Due Date logic:
    // If we paid enough to cover one EMI (with slight tolerance), bump the date.
    // AND if status is not completed.
    $nextDate = $ledger['next_due_date'];
    if ($status !== 'completed' && $amount >= ($monthlyEmi - 1)) {
        // Bump next due date by 1 month
        $nextDate = date('Y-m-d', strtotime($nextDate . ' + 1 month'));
    }

    // 3. Update Ledger
    $stmtUpd = $conn->prepare("UPDATE emi_ledgers SET paid_amount = :paid, status = :st, next_due_date = :nd WHERE id = :lid");
    $stmtUpd->execute([
        ':paid' => $newPaidAmount,
        ':st' => $status,
        ':nd' => $nextDate,
        ':lid' => $ledgerId
    ]);

    $conn->commit();

    echo json_encode(["success" => true, "message" => "Payment successful"]);

} catch (Exception $e) {
    if ($conn->inTransaction()) {
        $conn->rollBack();
    }
    http_response_code(400);
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>