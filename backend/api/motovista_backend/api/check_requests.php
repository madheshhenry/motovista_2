<?php
require_once '../config/db_connect.php';
try {
    $stmt = $conn->query("SELECT id, customer_id, bike_name, status FROM customer_requests ORDER BY id DESC LIMIT 5");
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);
    file_put_contents('customer_requests_debug.json', json_encode($data, JSON_PRETTY_PRINT));
    echo "Exported requests.\n";
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>