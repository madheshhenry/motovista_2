<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $sql = "CREATE TABLE IF NOT EXISTS `customer_requests` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `customer_id` int(11) NOT NULL,
        `customer_name` varchar(255) NOT NULL,
        `customer_phone` varchar(20) NOT NULL,
        `customer_profile` varchar(500) DEFAULT NULL,
        `bike_id` int(11) NOT NULL,
        `bike_name` varchar(255) NOT NULL,
        `bike_variant` varchar(255) NOT NULL,
        `bike_color` varchar(100) NOT NULL,
        `bike_price` varchar(50) NOT NULL,
        `status` varchar(50) DEFAULT 'Pending',
        `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
        PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

    $conn->exec($sql);

    echo json_encode(["success" => true, "message" => "Table 'customer_requests' created successfully"]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>