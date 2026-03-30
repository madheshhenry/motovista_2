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
        throw new Exception("Missing request_id");
    }

    $requestId = $data['request_id'];

    $sql = "DELETE FROM bike_requests WHERE id = :id";
    $stmt = $conn->prepare($sql);

    if ($stmt->execute([':id' => $requestId])) {
        echo json_encode(["success" => true, "message" => "Bike request deleted successfully"]);
    } else {
        throw new Exception("Database Delete Failed");
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>