<?php
require_once '../config/db_connect.php';

try {
    $sql = "CREATE TABLE IF NOT EXISTS `insurance_ledger` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `order_id` int(11) NOT NULL,
        `customer_id` int(11) NOT NULL,
        `customer_name` varchar(255) NOT NULL,
        `bike_name` varchar(255) NOT NULL,
        `policy_number` varchar(100) DEFAULT 'PENDING',
        `full_insurance_expiry` date NOT NULL,
        `third_party_expiry` date NOT NULL,
        `status` enum('Active', 'Expired', 'Expiring Soon') DEFAULT 'Active',
        `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
        PRIMARY KEY (`id`),
        UNIQUE KEY `unique_order` (`order_id`),
        KEY `customer_id` (`customer_id`),
        CONSTRAINT `fk_insurance_order` FOREIGN KEY (`order_id`) REFERENCES `customer_requests` (`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

    $conn->exec($sql);
    echo "Table 'insurance_ledger' created successfully.";

} catch (PDOException $e) {
    echo "Error creating table: " . $e->getMessage();
}
?>