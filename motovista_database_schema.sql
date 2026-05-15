-- MotoVista Full Database Schema
-- Last Updated: 2026-05-15

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

-- 1. Admins Table
CREATE TABLE IF NOT EXISTS `admins` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `token` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `otp` varchar(6) DEFAULT NULL,
  `otp_expiry` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Default Admin User (Password: admin123)
INSERT INTO `admins` (`id`, `username`, `email`, `password`) VALUES
(1, 'admin', 'admin@motovista.com', '$2y$10$1lVQddmSU84UZpT.Rnzbq.O13W9T44hQMGdRmSFAigi37fMGiJggm');

-- 2. Customers Table
CREATE TABLE IF NOT EXISTS `customers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `password` varchar(255) NOT NULL,
  `status` enum('active','inactive','pending','banned') DEFAULT 'pending',
  `email_verified` tinyint(1) DEFAULT 0,
  `phone_verified` tinyint(1) DEFAULT 0,
  `email_verification_token` varchar(255) DEFAULT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL,
  `profile_image` varchar(255) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `house_no` varchar(50) DEFAULT NULL,
  `street` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `state` varchar(50) DEFAULT NULL,
  `pincode` varchar(10) DEFAULT NULL,
  `driver_license_no` varchar(50) DEFAULT NULL,
  `aadhar_front` varchar(255) DEFAULT NULL,
  `aadhar_back` varchar(255) DEFAULT NULL,
  `pan` varchar(50) DEFAULT NULL,
  `is_profile_completed` tinyint(1) DEFAULT 0,
  `otp` varchar(6) DEFAULT NULL,
  `otp_expiry` datetime DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Bike Models Table
CREATE TABLE IF NOT EXISTS `bike_models` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `brand` varchar(100) NOT NULL,
    `model_name` varchar(100) NOT NULL,
    `model_year` varchar(4) NOT NULL,
    `engine_cc` varchar(50) NOT NULL,
    `fuel_type` varchar(50) NOT NULL,
    `transmission` varchar(50) NOT NULL,
    `mileage` varchar(50),
    `fuel_tank_capacity` varchar(50),
    `kerb_weight` varchar(50),
    `seat_height` varchar(50),
    `ground_clearance` varchar(50),
    `max_torque` varchar(100),
    `max_power` varchar(100),
    `warranty_period` varchar(100),
    `free_services` varchar(100),
    `mandatory_fittings` JSON,
    `additional_fittings` JSON,
    `invoice_legal_notes` JSON,
    `showroom_bank_details` JSON,
    `created_at` timestamp DEFAULT current_timestamp(),
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_model` (`brand`, `model_name`, `model_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Bike Variants Table
CREATE TABLE IF NOT EXISTS `bike_variants` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `model_id` int(11) NOT NULL,
    `variant_name` varchar(100) NOT NULL,
    `price_details` JSON NOT NULL,
    `brakes_wheels` JSON NOT NULL,
    `colors` JSON NOT NULL,
    `custom_sections` JSON,
    `created_at` timestamp DEFAULT current_timestamp(),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`model_id`) REFERENCES `bike_models`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Customer Requests (Orders) Table
CREATE TABLE IF NOT EXISTS `customer_requests` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. EMI Ledgers Table
CREATE TABLE IF NOT EXISTS `emi_ledgers` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `request_id` int(11),
    `title` varchar(255),
    `total_amount` decimal(10,2) NOT NULL,
    `paid_amount` decimal(10,2) DEFAULT 0.00,
    `emi_monthly_amount` decimal(10,2) NOT NULL,
    `interest_rate` decimal(5,2) DEFAULT 0.00,
    `duration_months` int(11) NOT NULL,
    `start_date` date,
    `next_due_date` date,
    `status` enum('active', 'completed', 'defaulted') DEFAULT 'active',
    `created_at` timestamp DEFAULT current_timestamp(),
    `updated_at` timestamp DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. EMI Payments Table
CREATE TABLE IF NOT EXISTS `emi_payments` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `ledger_id` int(11) NOT NULL,
    `amount_paid` decimal(10,2) NOT NULL,
    `payment_date` date NOT NULL,
    `payment_mode` varchar(50) DEFAULT 'Cash',
    `transaction_reference` varchar(255),
    `remarks` text,
    `created_at` timestamp DEFAULT current_timestamp(),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`ledger_id`) REFERENCES `emi_ledgers`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Registration Ledger (RTO Tracking) Table
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

-- 9. Customer Notifications Table
CREATE TABLE IF NOT EXISTS `customer_notifications` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) NOT NULL,
    `title` varchar(255) NOT NULL,
    `message` text NOT NULL,
    `type` varchar(50) DEFAULT 'system',
    `is_read` tinyint(1) DEFAULT 0,
    `created_at` timestamp DEFAULT current_timestamp(),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `customers`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

COMMIT;
