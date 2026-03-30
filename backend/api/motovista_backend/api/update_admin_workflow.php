<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    // 1. Authorization
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';
    if (!$authHeader)
        throw new Exception("Authorization token missing");

    $token = str_replace('Bearer ', '', $authHeader);
    $payload = (new JWT_HELPER())->validateToken($token);
    if (!$payload || $payload['role'] !== 'admin')
        throw new Exception("Unauthorized access or not an admin");

    $adminId = $payload['sub']; // 'sub' stores ID usually

    // 2. Parse Input
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    // stage can be string or null ("" or "null" string from client?)
    // active_order_id can be int or null

    $workflowStage = isset($data['workflow_stage']) ? $data['workflow_stage'] : null;
    $activeOrderId = isset($data['active_order_id']) ? $data['active_order_id'] : null;

    if ($workflowStage === "NULL")
        $workflowStage = null;
    if ($activeOrderId === -1 || $activeOrderId === "NULL")
        $activeOrderId = null;

    // 3. Update DB
    $sql = "UPDATE admins SET workflow_stage = :stage, active_order_id = :oid WHERE id = :id";
    $stmt = $conn->prepare($sql);
    $stmt->execute([
        ':stage' => $workflowStage,
        ':oid' => $activeOrderId,
        ':id' => $adminId
    ]);

    echo json_encode(["success" => true, "message" => "Workflow updated"]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>