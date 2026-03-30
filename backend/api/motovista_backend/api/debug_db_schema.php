<?php
header('Content-Type: text/plain');
require_once '../config/db_connect.php';

echo "Database Connection: " . ($conn ? "OK" : "FAILED") . "\n";

try {
    $stmt = $conn->query("SHOW TABLES");
    echo "Tables:\n";
    print_r($stmt->fetchAll(PDO::FETCH_COLUMN));

    $tables = ['bikes', 'bike_models', 'bike_variants'];
    foreach ($tables as $table) {
        $stmt = $conn->query("SHOW TABLES LIKE '$table'");
        if ($stmt->rowCount() > 0) {
            echo "\nColumns in $table:\n";
            $stmtCol = $conn->query("DESCRIBE $table");
            print_r($stmtCol->fetchAll(PDO::FETCH_ASSOC));

            $stmtCount = $conn->query("SELECT COUNT(*) FROM $table");
            echo "Row count in $table: " . $stmtCount->fetchColumn() . "\n";
        } else {
            echo "\nTable $table DOES NOT EXIST.\n";
        }
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage() . "\n";
}
?>