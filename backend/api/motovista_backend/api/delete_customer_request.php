<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

// Disable error display
ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception("Method Not Allowed");
    }

    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!$data || !isset($data['request_id'])) {
        throw new Exception("Missing request_id");
    }

    $requestId = $data['request_id'];

    $sql = "DELETE FROM customer_requests WHERE id = :id";
    $stmt = $conn->prepare($sql);

    if ($stmt->execute([':id' => $requestId])) {
        echo json_encode(["success" => true, "message" => "Request deleted successfully"]);
    } else {
        throw new Exception("Database Delete Failed");
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>