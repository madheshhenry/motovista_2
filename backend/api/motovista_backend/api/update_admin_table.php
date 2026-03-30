<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // Add columns if they don't exist
    $sql = "ALTER TABLE admins 
            ADD COLUMN IF NOT EXISTS otp VARCHAR(6) NULL,
            ADD COLUMN IF NOT EXISTS otp_expiry DATETIME NULL";

    $conn->exec($sql);

    echo json_encode(["success" => true, "message" => "Admins table updated for OTP"]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
