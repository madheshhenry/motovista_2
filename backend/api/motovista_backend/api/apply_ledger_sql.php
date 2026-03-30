<?php
require_once '../config/db_connect.php';

try {
    $sql = file_get_contents('registration_ledger.sql');
    $conn->exec($sql);
    echo "Table registration_ledger created successfully.";
} catch (PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>