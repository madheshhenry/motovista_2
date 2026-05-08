<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

// Error handling
error_reporting(E_ALL);
ini_set('display_errors', 0);

$MASTER_EMAIL = "mmadhesh225@gmail.com";
require_once '../config/email_config.php';

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

    // 🛡️ CHECK APPROVAL
    if ($admin['is_approved'] != 1) {
        throw new Exception("Your account is pending approval from Master Admin.");
    }

    if ($admin['otp'] !== $otp) {
        throw new Exception("Invalid OTP");
    }

    if (strtotime($admin['otp_expiry']) < time()) {
        throw new Exception("OTP Expired");
    }

    // 🗝️ GENERATE NEW SESSION ID (for Single Session)
    $newSessionId = bin2hex(random_bytes(16));

    // 2. Update session and Clear OTP
    $clearStmt = $conn->prepare("UPDATE admins SET otp = NULL, otp_expiry = NULL, last_session_id = ? WHERE id = ?");
    $clearStmt->execute([$newSessionId, $admin['id']]);

    // 📧 LOGIN NOTIFICATION TO MASTER (Via Mail Bridge)
    if (defined('USE_MAIL_BRIDGE') && USE_MAIL_BRIDGE) {
        $bridgeData = [
            'to' => $MASTER_EMAIL,
            'subject' => "Admin Login Alert: " . $admin['username'],
            'message' => "Admin " . $admin['username'] . " has logged in from a new device at " . date('Y-m-d H:i:s')
        ];
        $ch = curl_init(MAIL_BRIDGE_URL);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $bridgeData);
        curl_exec($ch);
        curl_close($ch);
    }

    // 3. Generate Token with Session ID
    $jwtHelper = new JWT_HELPER();
    $jwt = $jwtHelper->generateToken($admin['id'], $admin['email'], 'admin', $newSessionId);

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
