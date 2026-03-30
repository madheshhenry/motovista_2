<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $sql = "SELECT * FROM bike_requests ORDER BY created_at DESC";
    $stmt = $conn->prepare($sql);
    $stmt->execute();

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