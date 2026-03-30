CREATE TABLE IF NOT EXISTS `registration_ledger` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `customer_name` varchar(255) NOT NULL,
  `bike_name` varchar(255) NOT NULL,
  `step_1_status` enum('pending','completed') DEFAULT 'pending',
  `step_2_status` enum('locked','pending','completed') DEFAULT 'locked',
  `step_3_status` enum('locked','pending','completed') DEFAULT 'locked',
  `step_4_status` enum('locked','pending','completed') DEFAULT 'locked',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `fk_ledger_order` FOREIGN KEY (`order_id`) REFERENCES `customer_requests` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
