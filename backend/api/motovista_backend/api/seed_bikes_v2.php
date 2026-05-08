<?php
require_once '../config/db_connect.php';

try {
    $conn->beginTransaction();

    // 1. Delete existing data to start fresh
    $conn->exec("SET FOREIGN_KEY_CHECKS = 0;");
    $conn->exec("TRUNCATE TABLE bike_variants;");
    $conn->exec("TRUNCATE TABLE bike_models;");
    $conn->exec("SET FOREIGN_KEY_CHECKS = 1;");

    // 2. Insert Model
    $stmt = $conn->prepare("INSERT INTO bike_models (brand, model_name, model_year, engine_cc, fuel_type, transmission, mileage, fuel_tank_capacity, kerb_weight, seat_height, ground_clearance, max_torque, max_power, warranty_period, free_services) 
            VALUES ('Yamaha', 'R15 V4', '2023', '155', 'Petrol', 'Manual', '45', '11', '142', '815', '170', '14.2 Nm @ 7500 rpm', '18.4 PS @ 10000 rpm', '2 Years / 30,000 km', '3')");
    $stmt->execute();
    $modelId = $conn->lastInsertId();

    // 3. Insert Variant
    $priceDetails = json_encode(['ex_showroom' => '1,81,700', 'insurance' => '12,000', 'registration' => '15,000', 'ltrt' => '2,000', 'total_on_road' => '2,10,700']);
    $brakesWheels = json_encode(['front_brake' => 'Disc', 'rear_brake' => 'Disc', 'braking_system' => 'Dual Channel ABS', 'wheel_type' => 'Alloy']);
    
    // Using existing local images from uploads/bikes/
    $colors = json_encode([
        [
            'color_name' => 'Racing Blue',
            'color_hex' => '#0000FF',
            'image_paths' => ['bike_69652f176e2fb.jpg', 'bike_69652f178180b.jpg']
        ],
        [
            'color_name' => 'Metallic Grey',
            'color_hex' => '#808080',
            'image_paths' => ['bike_69652f17a6ff5.jpg']
        ]
    ]);
    
    $customSections = json_encode([
        [
            'section_name' => 'Engine & Transmission',
            'fields' => [
                ['key' => 'Engine Type', 'value' => 'Liquid-cooled, 4-stroke, SOHC, 4-valve'],
                ['key' => 'Displacement', 'value' => '155 cc'],
                ['key' => 'Bore & Stroke', 'value' => '58.0 mm × 58.7 mm']
            ]
        ]
    ]);

    $stmt = $conn->prepare("INSERT INTO bike_variants (model_id, variant_name, price_details, brakes_wheels, colors, custom_sections) 
            VALUES (?, 'Racing Blue', ?, ?, ?, ?)");
    $stmt->execute([$modelId, $priceDetails, $brakesWheels, $colors, $customSections]);

    $conn->commit();
    echo "Successfully seeded proper data for Yamaha R15 V4 with local images!";

} catch (Exception $e) {
    $conn->rollBack();
    echo "Error: " . $e->getMessage();
}
?>
