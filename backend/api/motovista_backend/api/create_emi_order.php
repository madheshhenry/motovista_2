<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $data = json_decode(file_get_contents("php://input"), true);

    if (!isset($data['request_id']) || !isset($data['total_amount']) || !isset($data['emi_monthly_amount']) || !isset($data['duration_months'])) {
        throw new Exception("Missing required fields");
    }

    $request_id = $data['request_id'];
    $total_amount = $data['total_amount'];
    $emi_monthly_amount = $data['emi_monthly_amount'];
    $duration_months = $data['duration_months'];
    $interest_rate = isset($data['interest_rate']) ? $data['interest_rate'] : 0.00;

    // Calculate dates
    $start_date = date('Y-m-d');
    // First due date is usually 1 month from now or same day next month
    $next_due_date = date('Y-m-d', strtotime('+1 month'));

    // Insert
    $sql = "INSERT INTO emi_ledgers (request_id, total_amount, emi_monthly_amount, duration_months, interest_rate, start_date, next_due_date, status) 
            VALUES (:rid, :amt, :emi, :dur, :rate, :start, :next, 'active')";

    $stmt = $conn->prepare($sql);
    $stmt->execute([
        ':rid' => $request_id,
        ':amt' => $total_amount,
        ':emi' => $emi_monthly_amount,
        ':dur' => $duration_months,
        ':rate' => $interest_rate,
        ':start' => $start_date,
        ':next' => $next_due_date
    ]);

    // Update customer request status to completed
    $updateSql = "UPDATE customer_requests SET status = 'completed' WHERE id = :rid";
    $updateStmt = $conn->prepare($updateSql);
    $updateStmt->execute([':rid' => $request_id]);

    // Get inserted ID
    $ledger_id = $conn->lastInsertId();
    $order_reference = "EMI-" . date("Ymd") . "-" . str_pad($ledger_id, 4, "0", STR_PAD_LEFT);

    echo json_encode([
        "status" => "success",
        "message" => "EMI Order Created Successfully",
        "data" => [
            "ledger_id" => $ledger_id,
            "order_reference" => $order_reference
        ]
    ]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>