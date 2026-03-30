<?php
require_once __DIR__ . '/../config/db_connect.php';

try {
    // 1. Insert Dummy Bike
    $stmt = $conn->prepare("INSERT INTO bikes (id, model, brand, image_paths, engine_cc, mileage, condition_type, ex_showroom_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
    // Use ID 1 to match the existing request
    $result = $stmt->execute([
        1,
        'Yamaha R15 V4',
        'Yamaha',
        '["uploads/r15_blue.jpg"]',
        '155cc',
        '45 kmpl',
        'NEW',
        200000
    ]);

    if ($result) {
        echo "Inserted Dummy Bike (ID 1).\n";
    } else {
        echo "Failed to insert bike (might already exist).\n";
    }

    // 2. Update Customer Request with Profile
    $stmt2 = $conn->prepare("UPDATE customer_requests SET customer_profile = ? WHERE id = ?");
    $stmt2->execute(['uploads/profile_placeholder.jpg', 12]);
    echo "Updated Customer Request (ID 12) with profile image.\n";

} catch (Exception $e) {
    echo "Error: " . $e->getMessage() . "\n";
}
?>