<?php
require_once '../config/db_connect.php';

try {
    echo "Starting Migration...\n";

    // 1. Update customer_notifications
    echo "Updating customer_notifications table...\n";
    $conn->exec("ALTER TABLE customer_notifications ADD COLUMN IF NOT EXISTS target_screen VARCHAR(255) DEFAULT NULL");
    $conn->exec("ALTER TABLE customer_notifications ADD COLUMN IF NOT EXISTS item_id VARCHAR(20) DEFAULT NULL");

    // 2. Update admin_notifications
    echo "Updating admin_notifications table...\n";
    $conn->exec("ALTER TABLE admin_notifications ADD COLUMN IF NOT EXISTS target_screen VARCHAR(255) DEFAULT NULL");
    $conn->exec("ALTER TABLE admin_notifications ADD COLUMN IF NOT EXISTS item_id VARCHAR(20) DEFAULT NULL");

    echo "Migration completed successfully!\n";
} catch (Exception $e) {
    echo "Migration failed: " . $e->getMessage() . "\n";
}
?>