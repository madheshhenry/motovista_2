<?php
require_once '../config/db_connect.php';

try {
    $sql = "CREATE TABLE IF NOT EXISTS customer_notifications (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        title VARCHAR(255) NOT NULL,
        message TEXT NOT NULL,
        type VARCHAR(50) DEFAULT 'system',
        is_read TINYINT(1) DEFAULT 0,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    )";

    $conn->exec($sql);
    echo json_encode(["success" => true, "message" => "Table 'customer_notifications' created successfully"]);

    // Add some initial notifications for a specific user if provided
    if (isset($_GET['seed_user_id'])) {
        $userId = $_GET['seed_user_id'];
        $stmt = $conn->prepare("INSERT INTO customer_notifications (user_id, title, message, type) VALUES (?, ?, ?, ?)");

        $seedData = [
            [$userId, "Welcome to MotoVista!", "Thank you for joining our community. Explore our latest bike collections now.", "system"],
            [$userId, "Profile Complete", "Your profile has been successfully verified. You can now request bike bookings.", "system"],
            [$userId, "Exclusive Offer", "Get 15% off on your first bike service. Use code MOTO15.", "offer"]
        ];

        foreach ($seedData as $data) {
            $stmt->execute($data);
        }
        echo json_encode(["success" => true, "message" => "Seed data added for user $userId"]);
    }

} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Error creating table: " . $e->getMessage()]);
}
?>