<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
try {
    $stmt1 = $conn->query("SHOW COLUMNS FROM bikes");
    $bikes = $stmt1->fetchAll(PDO::FETCH_ASSOC);
    $stmt2 = $conn->query("SHOW COLUMNS FROM customer_requests");
    $requests = $stmt2->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode(["bikes" => $bikes, "requests" => $requests]);
} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>
