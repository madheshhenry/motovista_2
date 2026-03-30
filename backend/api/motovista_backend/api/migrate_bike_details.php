<?php
require_once __DIR__ . '/../config/db_connect.php';

try {
    // 1. Add columns to 'bikes' table if they don't exist
    $columns = ['color', 'variant', 'on_road_price'];
    foreach ($columns as $col) {
        $check = $conn->query("SHOW COLUMNS FROM bikes LIKE '$col'");
        if ($check->rowCount() == 0) {
            $conn->exec("ALTER TABLE bikes ADD COLUMN $col VARCHAR(100) DEFAULT NULL");
            echo "Added '$col' column to bikes table.\n";
        }
    }

    // 2. Update Bike ID 1 with CORRECT Data (Matching the Image/Context)
    // Image is R15 V4 Racing Blue
    // Price ~ 2.15 Lakhs
    $stmt = $conn->prepare("UPDATE bikes SET 
        color = ?, 
        variant = ?, 
        on_road_price = ? 
        WHERE id = ?");

    // Racing Blue Hex: #004CA3 (Approximate Yamaha Blue)
    $stmt->execute(['#004CA3', 'Racing Blue - M', '215000.00', 1]);

    echo "Updated Bike ID 1: Color=#004CA3 (Racing Blue), Variant=Racing Blue - M, Price=215000.00\n";

} catch (Exception $e) {
    echo "Error: " . $e->getMessage() . "\n";
}
?>