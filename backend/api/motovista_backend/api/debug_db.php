<?php
echo "Starting debug...\n";
require_once '../config/db_connect.php';
if (!isset($conn)) {
    die("Connection variable \$conn is not set!\n");
}
echo "Connected to: $db_name\n";
try {
    $stmt = $conn->query("SHOW TABLES");
    $tables = $stmt->fetchAll(PDO::FETCH_COLUMN);
    echo "Tables count: " . count($tables) . "\n";
    print_r($tables);
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>