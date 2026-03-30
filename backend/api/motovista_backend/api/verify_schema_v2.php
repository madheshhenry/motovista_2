<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);
require_once '../config/db_connect.php';

try {
    echo "Bikes Table Columns: ";
    $stmt = $conn->query("SHOW COLUMNS FROM bikes");
    $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($columns as $col) {
        echo $col['Field'] . ", ";
    }
    echo "\n";
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>