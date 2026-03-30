<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
  $sql = "CREATE TABLE IF NOT EXISTS `bikes` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `brand` varchar(50) NOT NULL,
      `model` varchar(100) NOT NULL,
      `variant` varchar(100) DEFAULT NULL,
      `year` varchar(4) DEFAULT NULL,
      `engine_cc` varchar(20) DEFAULT NULL,
      `fuel_type` varchar(20) DEFAULT NULL,
      `transmission` varchar(20) DEFAULT NULL,
      `braking_type` varchar(20) DEFAULT NULL,
      `on_road_price` decimal(10,2) DEFAULT NULL,
      `ex_showroom_price` decimal(10,2) DEFAULT NULL,
      `insurance_price` decimal(10,2) DEFAULT NULL,
      `registration_price` decimal(10,2) DEFAULT NULL,
      `ltrt_price` decimal(10,2) DEFAULT NULL,
      `mileage` varchar(50) DEFAULT NULL,
      `fuel_tank_capacity` varchar(50) DEFAULT NULL,
      `kerb_weight` varchar(50) DEFAULT NULL,
      `seat_height` varchar(50) DEFAULT NULL,
      `ground_clearance` varchar(50) DEFAULT NULL,
      `warranty` varchar(50) DEFAULT NULL,
      `free_services` varchar(100) DEFAULT NULL,
      `registration_proof` varchar(255) DEFAULT NULL,
      `price_disclaimer` varchar(255) DEFAULT NULL,
      `condition_type` varchar(20) DEFAULT 'NEW',
      `features` text DEFAULT NULL,
      `image_paths` text DEFAULT NULL,
      `date` varchar(20) DEFAULT NULL,
      `engine_number` varchar(50) DEFAULT NULL,
      `chassis_number` varchar(50) DEFAULT NULL,
      `chassis_number` varchar(50) DEFAULT NULL,
      `colors` text DEFAULT NULL,
      `custom_fittings` text DEFAULT NULL,
      `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

  $conn->exec($sql);

  echo json_encode(["success" => true, "message" => "Bikes table created successfully"]);

} catch (Exception $e) {
  echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
