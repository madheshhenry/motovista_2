<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

$input = json_decode(file_get_contents('php://input'), TRUE);

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = $input['email'] ?? '';
    $otp = $input['otp'] ?? '';

    if (empty($email) || empty($otp)) {
        echo json_encode(["success" => false, "message" => "Email and OTP are required."]);
        exit;
    }

    try {
        // 1. Verify OTP
        $stmt = $conn->prepare("SELECT * FROM customers WHERE email = ? AND otp = ? AND otp_expiry > NOW()");
        $stmt->execute([$email, $otp]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$user) {
            echo json_encode(["success" => false, "message" => "Invalid or expired verification code."]);
            exit;
        }

        // 2. Success - Clear OTP and generate JWT
        $clearStmt = $conn->prepare("UPDATE customers SET otp = NULL, otp_expiry = NULL WHERE id = ?");
        $clearStmt->execute([$user['id']]);

        // Generate JWT Token
        $jwt = new JWT_HELPER();
        $token = $jwt->generateToken($user['id'], $user['email']);

        echo json_encode([
            "success" => true,
            "message" => "Verification successful!",
            "data" => [
                "token" => $token,
                "customer" => [
                    "id" => (int) $user['id'],
                    "full_name" => $user['full_name'],
                    "email" => $user['email'],
                    "phone" => $user['phone'],
                    "is_profile_completed" => (bool) $user['is_profile_completed'],
                    "created_at" => $user['created_at'],
                    "profile_image" => $user['profile_image']
                ]
            ]
        ]);

    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(["success" => false, "message" => "Server error: " . $e->getMessage()]);
    }
}
?>
