<?php
require_once '../config/db_connect.php';
try {
    $stmt = $conn->query("DESCRIBE customer_notifications");
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);
    file_put_contents('customer_notifications_schema.json', json_encode($data, JSON_PRETTY_PRINT));
    echo "Schema exported.\n";
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>