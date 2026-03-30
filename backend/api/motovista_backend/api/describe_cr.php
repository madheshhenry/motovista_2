<?php
require_once '../config/db_connect.php';
header('Content-Type: text/plain');

$table = 'customer_requests';
echo "--- DESCRIBE $table ---\n";
try {
    $stmt = $conn->query("DESCRIBE $table");
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        print_r($row);
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage() . "\n";
}
?>