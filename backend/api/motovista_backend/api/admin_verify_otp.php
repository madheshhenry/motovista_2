<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

// Error handling
error_reporting(E_ALL);
ini_set('display_errors', 0);

try {
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!isset($data['email']) || !isset($data['otp'])) {
        throw new Exception("Email and OTP are required");
    }

    $email = trim($data['email']);
    $otp = trim($data['otp']);

    // 1. Verify OTP
    $stmt = $conn->prepare("SELECT * FROM admins WHERE email = ?");
    $stmt->execute([$email]);
    $admin = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$admin) {
        throw new Exception("Admin not found");
    }

    if ($admin['otp'] !== $otp) {
        throw new Exception("Invalid OTP");
    }

    if (strtotime($admin['otp_expiry']) < time()) {
        throw new Exception("OTP Expired");
    }

    // 2. Clear OTP (Single use)
    $clearStmt = $conn->prepare("UPDATE admins SET otp = NULL, otp_expiry = NULL WHERE id = ?");
    $clearStmt->execute([$admin['id']]);

    // 3. Generate Token
    // We reuse the Customer structure or create a specific Admin payload
    // Let's stick to the existing structure if possible, but role = 'admin'

    // Existing users logic might rely on 'id' and 'email'.
    // NOTE: Android app expects:
    /*
        "data": {
            "token": "...",
            "customer": { ... }
        }
    */

    $jwtHelper = new JWT_HELPER();
    $jwt = $jwtHelper->generateToken($admin['id'], $admin['email'], 'admin');

    // 4. Return Response
    echo json_encode([
        "success" => true,
        "message" => "Login Successful",
        "data" => [
            "token" => $jwt,
            "customer" => [ // Mapping admin to 'customer' object for app compatibility
                "id" => (int) $admin['id'],
                "full_name" => $admin['username'], // Mapping username to full_name
                "email" => $admin['email'],
                "phone" => "",
                "role" => "admin",
                "workflow_stage" => $admin['workflow_stage'],
                "active_order_id" => $admin['active_order_id']
            ]
        ]
    ]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
} catch (Throwable $e) {
    echo json_encode(["success" => false, "message" => "Error: " . $e->getMessage()]);
}
