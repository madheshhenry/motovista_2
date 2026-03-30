<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $sql = "CREATE TABLE IF NOT EXISTS `bike_requests` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `brand` varchar(100) NOT NULL,
        `model` varchar(100) NOT NULL,
        `features` text DEFAULT NULL,
        `full_name` varchar(255) NOT NULL,
        `mobile_number` varchar(20) NOT NULL,
        `email` varchar(255) DEFAULT NULL,
        `status` varchar(50) DEFAULT 'Pending',
        `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
        PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

    $conn->exec($sql);

    echo json_encode(["success" => true, "message" => "Table 'bike_requests' created successfully"]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>