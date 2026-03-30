<?php
// Simple script to run ALTER TABLE commands
require_once '../config/db_connect.php';

try {
    $sqls = [
        "ALTER TABLE bikes ADD COLUMN front_brake VARCHAR(50) DEFAULT NULL",
        "ALTER TABLE bikes ADD COLUMN rear_brake VARCHAR(50) DEFAULT NULL",
        "ALTER TABLE bikes ADD COLUMN abs_type VARCHAR(50) DEFAULT NULL",
        "ALTER TABLE bikes ADD COLUMN wheel_type VARCHAR(50) DEFAULT NULL"
    ];

    foreach ($sqls as $sql) {
        try {
            $conn->exec($sql);
            echo "Executed: $sql\n";
        } catch (PDOException $e) {
            // Ignore if column exists (Code 42S21 in MySQL usually, or just check message)
            echo "Skipped (or error): " . $e->getMessage() . "\n";
        }
    }
    echo "Schema update completed.\n";

} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>