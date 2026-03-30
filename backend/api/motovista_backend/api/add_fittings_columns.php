<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // Add colors column
    echo "Adding colors column...\n";
    $sql0 = "ALTER TABLE bikes ADD COLUMN colors TEXT DEFAULT NULL";
    try {
        $conn->exec($sql0);
        echo "Added colors column.\n";
    } catch (PDOException $e) {
        echo "colors column error (maybe exists): " . $e->getMessage() . "\n";
    }

    // Add custom_fittings column
    echo "Adding custom_fittings column...\n";
    $sql0b = "ALTER TABLE bikes ADD COLUMN custom_fittings TEXT DEFAULT NULL";
    try {
        $conn->exec($sql0b);
        echo "Added custom_fittings column.\n";
    } catch (PDOException $e) {
        echo "custom_fittings column error (maybe exists): " . $e->getMessage() . "\n";
    }

    // Add mandatory_fittings column
    echo "Adding mandatory_fittings column...\n";
    $sql1 = "ALTER TABLE bikes ADD COLUMN mandatory_fittings TEXT DEFAULT NULL";
    try {
        $conn->exec($sql1);
        echo "Added mandatory_fittings column.\n";
    } catch (PDOException $e) {
        echo "mandatory_fittings column error (maybe exists): " . $e->getMessage() . "\n";
    }

    // Add additional_fittings column
    echo "Adding additional_fittings column...\n";
    $sql2 = "ALTER TABLE bikes ADD COLUMN additional_fittings TEXT DEFAULT NULL";
    try {
        $conn->exec($sql2);
        echo "Added additional_fittings column.\n";
    } catch (PDOException $e) {
        echo "additional_fittings column error (maybe exists): " . $e->getMessage() . "\n";
    }

    echo json_encode(["status" => "success", "message" => "Database updated successfully"]);

} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>