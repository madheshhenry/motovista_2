<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception("Method Not Allowed");
    }

    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!$data || !isset($data['id'])) {
        throw new Exception("Missing notification ID");
    }

    $id = $data['id'];

    $sql = "DELETE FROM customer_notifications WHERE id = :id";
    $stmt = $conn->prepare($sql);
    
    if ($stmt->execute([':id' => $id])) {
        echo json_encode(["success" => true, "message" => "Notification deleted successfully"]);
    } else {
        throw new Exception("Failed to delete notification");
    }

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>
