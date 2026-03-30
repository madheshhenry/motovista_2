<?php
require_once '../config/db_connect.php';

try {
    // DROP tables if exist to reset schema correctly
    $conn->exec("DROP TABLE IF EXISTS bike_variants");
    $conn->exec("DROP TABLE IF EXISTS bike_models");

    // 1. Create bike_models table (Parent)
    // moved fittings here
    $sqlModels = "CREATE TABLE IF NOT EXISTS bike_models (
        id INT AUTO_INCREMENT PRIMARY KEY,
        brand VARCHAR(100) NOT NULL,
        model_name VARCHAR(100) NOT NULL,
        model_year VARCHAR(4) NOT NULL,
        engine_cc VARCHAR(50) NOT NULL,
        fuel_type VARCHAR(50) NOT NULL,
        transmission VARCHAR(50) NOT NULL,
        mileage VARCHAR(50),
        fuel_tank_capacity VARCHAR(50),
        kerb_weight VARCHAR(50),
        seat_height VARCHAR(50),
        ground_clearance VARCHAR(50),
        max_torque VARCHAR(100),
        max_power VARCHAR(100),
        warranty_period VARCHAR(100),
        free_services VARCHAR(100),
        mandatory_fittings JSON,   -- Moved to Model Level
        additional_fittings JSON,  -- Moved to Model Level
        invoice_legal_notes JSON,
        showroom_bank_details JSON,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        UNIQUE KEY unique_model (brand, model_name, model_year)
    ) ENGINE=InnoDB;";

    $conn->exec($sqlModels);
    echo "Table 'bike_models' created successfully.\n";

    // 2. Create bike_variants table (Child)
    // removed fittings from here
    $sqlVariants = "CREATE TABLE IF NOT EXISTS bike_variants (
        id INT AUTO_INCREMENT PRIMARY KEY,
        model_id INT NOT NULL,
        variant_name VARCHAR(100) NOT NULL,
        price_details JSON NOT NULL, -- ex_showroom, insurance, registration, ltrt, total
        brakes_wheels JSON NOT NULL, -- front, rear, system, wheel, tyre
        colors JSON NOT NULL, -- Array of {name, code, images:[]}
        custom_sections JSON, -- User defined sections
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (model_id) REFERENCES bike_models(id) ON DELETE CASCADE
    ) ENGINE=InnoDB;";

    $conn->exec($sqlVariants);
    echo "Table 'bike_variants' created successfully.\n";

} catch (PDOException $e) {
    echo "Error creating tables: " . $e->getMessage();
}
?>