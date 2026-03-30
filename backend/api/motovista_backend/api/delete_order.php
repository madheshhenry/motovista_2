<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception("Method Not Allowed");
    }

    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!$data || !isset($data['request_id'])) {
        throw new Exception("Missing required field: request_id");
    }

    $requestId = $data['request_id'];

    $conn->beginTransaction();

    // 1. Find EMI ledgers related to this request
    $stmtEmi = $conn->prepare("SELECT id FROM emi_ledgers WHERE request_id = :rid");
    $stmtEmi->execute([':rid' => $requestId]);
    $emiLedgers = $stmtEmi->fetchAll(PDO::FETCH_COLUMN);

    if (!empty($emiLedgers)) {
        // 2. Delete EMI payments
        $placeholders = implode(',', array_fill(0, count($emiLedgers), '?'));
        $stmtDelPayments = $conn->prepare("DELETE FROM emi_payments WHERE ledger_id IN ($placeholders)");
        $stmtDelPayments->execute($emiLedgers);

        // 3. Delete EMI ledgers
        $stmtDelLedgers = $conn->prepare("DELETE FROM emi_ledgers WHERE request_id = :rid");
        $stmtDelLedgers->execute([':rid' => $requestId]);
    }

    // 4. Delete from registration_ledger
    $stmtDelReg = $conn->prepare("DELETE FROM registration_ledger WHERE order_id = :rid");
    $stmtDelReg->execute([':rid' => $requestId]);

    // 5. Delete from registration_status
    $stmtDelRegStatus = $conn->prepare("DELETE FROM registration_status WHERE request_id = :rid");
    $stmtDelRegStatus->execute([':rid' => $requestId]);

    // 6. Delete from customer_requests
    $stmtDelReq = $conn->prepare("DELETE FROM customer_requests WHERE id = :rid");
    $stmtDelReq->execute([':rid' => $requestId]);

    $conn->commit();

    echo json_encode(["success" => true, "message" => "Order and all related records deleted successfully"]);

} catch (Exception $e) {
    if (isset($conn))
        $conn->rollBack();
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>