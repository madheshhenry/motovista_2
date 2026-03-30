<?php
require_once '../config/db_connect.php';

try {
    $conn->exec("ALTER TABLE customers ADD COLUMN otp VARCHAR(6) NULL");
    $conn->exec("ALTER TABLE customers ADD COLUMN otp_expiry DATETIME NULL");
    echo json_encode(["success" => true, "message" => "OTP columns added successfully"]);
} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Error adding columns: " . $e->getMessage()]);
}
?>