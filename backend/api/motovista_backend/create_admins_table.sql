CREATE TABLE `admins` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `admins` (`id`, `username`, `email`, `password`, `created_at`, `otp`, `otp_expiry`) VALUES
(1, 'admin', 'admin@motovista.com', '$2y$10$1lVQddmSU84UZpT.Rnzbq.O13W9T44hQMGdRmSFAigi37fMGiJggm', '2025-01-05 01:22:14', NULL, NULL);
