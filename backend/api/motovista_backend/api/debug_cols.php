<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
try {
    $q1 = $conn->query("SHOW COLUMNS FROM customer_requests");
    $c1 = $q1->fetchAll(PDO::FETCH_COLUMN);
    $q2 = $conn->query("SHOW COLUMNS FROM users");
    $c2 = $q2->fetchAll(PDO::FETCH_COLUMN);
    echo json_encode(["customer_requests" => $c1, "users" => $c2]);
} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>
