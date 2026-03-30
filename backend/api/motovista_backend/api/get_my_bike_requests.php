<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception("Method Not Allowed");
    }

    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    $userId = $data['user_id'] ?? null;
    $mobileNumber = $data['mobile_number'] ?? null;

    if (!$userId && !$mobileNumber) {
        throw new Exception("Missing user identifier (user_id or mobile_number)");
    }

    // Select requests where user_id matches OR mobile number matches
    if ($userId) {
        $sql = "SELECT * FROM bike_requests WHERE user_id = :user_id OR mobile_number = :mobile_number ORDER BY created_at DESC";
        $stmt = $conn->prepare($sql);
        $stmt->execute([':user_id' => $userId, ':mobile_number' => $mobileNumber]);
    } else {
        $sql = "SELECT * FROM bike_requests WHERE mobile_number = :mobile_number ORDER BY created_at DESC";
        $stmt = $conn->prepare($sql);
        $stmt->execute([':mobile_number' => $mobileNumber]);
    }

    $requests = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        "success" => true,
        "data" => $requests
    ]);

} catch (Exception $e) {
    echo json_encode([
        "success" => false,
        "message" => "Error fetching requests: " . $e->getMessage()
    ]);
}
?>