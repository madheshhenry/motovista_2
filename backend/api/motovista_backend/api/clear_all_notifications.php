<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception("Method Not Allowed");
    }

    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!$data || !isset($data['customer_id'])) {
        throw new Exception("Missing customer_id");
    }

    $customerId = $data['customer_id'];

    $sql = "DELETE FROM customer_notifications WHERE customer_id = :cid";
    $stmt = $conn->prepare($sql);
    
    if ($stmt->execute([':cid' => $customerId])) {
        echo json_encode(["success" => true, "message" => "All notifications cleared"]);
    } else {
        throw new Exception("Failed to clear notifications");
    }

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>
