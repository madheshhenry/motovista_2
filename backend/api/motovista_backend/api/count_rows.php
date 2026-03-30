<?php
require_once '../config/db_connect.php';

$tables = ['bikes', 'customer_requests', 'bike_models', 'bike_variants'];

foreach ($tables as $t) {
    try {
        $count = $conn->query("SELECT COUNT(*) FROM $t")->fetchColumn();
        echo "Table '$t': $count rows\n";
    } catch (Exception $e) {
        echo "Table '$t': Error - " . $e->getMessage() . "\n";
    }
}
?>