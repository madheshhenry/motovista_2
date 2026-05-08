<?php
header("Content-Type: application/json; charset=UTF-8");
ini_set('display_errors', 0);

require_once '../config/db_connect.php';
require_once '../config/email_config.php';

try {
    $data = json_decode(file_get_contents("php://input"));

    if (!isset($data->email) || !isset($data->otp)) {
        echo json_encode(["success" => false, "message" => "Missing email or OTP"]);
        exit;
    }

    $email = trim($data->email);
    $otp = trim($data->otp);

    $stmt = $conn->prepare("SELECT * FROM admins WHERE email = ?");
    $stmt->execute([$email]);
    $admin = $stmt->fetch();

    if (!$admin) {
        echo json_encode(["success" => false, "message" => "Admin not found"]);
        exit;
    }

    if ($admin['is_email_verified'] == 1) {
        echo json_encode(["success" => false, "message" => "Email already verified"]);
        exit;
    }

    if ($admin['otp'] !== $otp) {
        echo json_encode(["success" => false, "message" => "Invalid OTP"]);
        exit;
    }

    if (strtotime($admin['otp_expiry']) < time()) {
        echo json_encode(["success" => false, "message" => "OTP Expired. Please register again."]);
        exit;
    }

    // Verify
    $update = $conn->prepare("UPDATE admins SET is_email_verified = 1, otp = NULL, otp_expiry = NULL WHERE id = ?");
    if ($update->execute([$admin['id']])) {
        
        // Notify Master Admin
        if (defined('USE_MAIL_BRIDGE') && USE_MAIL_BRIDGE) {
            $masterEmail = defined('SMTP_USERNAME') ? SMTP_USERNAME : "mmadhesh225@gmail.com";
            
            $bridgeData = [
                'to' => $masterEmail,
                'subject' => "New Admin Registration Request: " . $admin['username'],
                'message' => "A new sub-admin has verified their email and is waiting for your approval.\n\nUsername: " . $admin['username'] . "\nEmail: " . $admin['email'] . "\n\nPlease approve them to allow login."
            ];
            $ch = curl_init(MAIL_BRIDGE_URL);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_POSTFIELDS, $bridgeData);
            curl_exec($ch);
            curl_close($ch);
        }

        echo json_encode(["success" => true, "message" => "Email verified. Request sent to Master Admin."]);
    } else {
        echo json_encode(["success" => false, "message" => "Verification failed."]);
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => "Server Error: " . $e->getMessage()]);
}
?>
