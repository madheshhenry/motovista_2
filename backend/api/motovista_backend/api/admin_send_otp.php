<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/email_config.php';

// PHPMailer Namespace Setup
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require_once '../libs/PHPMailer/Exception.php';
require_once '../libs/PHPMailer/PHPMailer.php';
require_once '../libs/PHPMailer/SMTP.php';

// Error handling
error_reporting(E_ALL);
ini_set('display_errors', 0); // Hide errors, return JSON

try {
    // Get JSON input
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!isset($data['email']) || !isset($data['password'])) {
        throw new Exception("Email and Password are required");
    }

    $email = trim($data['email']);
    $password = trim($data['password']);

    // 1. Validate Admin Credentials
    $stmt = $conn->prepare("SELECT id, password FROM admins WHERE email = ? OR username = ?");
    $stmt->execute([$email, $email]); // Using same input for both since it's Email/Username
    $admin = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$admin || !password_verify($password, $admin['password'])) {
        throw new Exception("Invalid credentials");
    }

    // 2. Generate OTP
    $otp = rand(100000, 999999);
    $expiry = date("Y-m-d H:i:s", strtotime("+10 minutes"));

    // 3. Save OTP to DB
    $updateStmt = $conn->prepare("UPDATE admins SET otp = ?, otp_expiry = ? WHERE id = ?");
    $updateStmt->execute([$otp, $expiry, $admin['id']]);

    // 4. Send Email (Check for Mail Bridge first)
    if (defined('USE_MAIL_BRIDGE') && USE_MAIL_BRIDGE) {
        $bridgeData = json_encode([
            'to' => $email,
            'subject' => 'Admin Login OTP - MotoVista',
            'body' => "Admin Login Verification. Your OTP is: $otp. This OTP is valid for 10 minutes."
        ]);
        
        $ch = curl_init(MAIL_BRIDGE_URL);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $bridgeData);
        curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
        curl_exec($ch);
        curl_close($ch);
        
        echo json_encode(["success" => true, "message" => "OTP sent successfully via Mail Bridge"]);
        exit;
    }

    // Fallback to PHPMailer if bridge is off
    $mail = new PHPMailer(true);

    // Server settings
    $mail->isSMTP();
    $mail->Host = SMTP_HOST;
    $mail->SMTPAuth = true;
    $mail->Username = SMTP_USERNAME;
    $mail->Password = SMTP_PASSWORD;
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port = SMTP_PORT;

    // Recipients
    $mail->setFrom(SMTP_USERNAME, 'MotoVista Admin');
    $mail->addAddress($email);

    // Content
    $mail->isHTML(true);
    $mail->Subject = 'Admin Login OTP - MotoVista';
    $mail->Body = "
        <h3>Admin Login Verification</h3>
        <p>Your OTP is: <strong>$otp</strong></p>
        <p>This OTP is valid for 10 minutes.</p>
    ";

    $mail->send();

    echo json_encode(["success" => true, "message" => "OTP sent successfully to your email"]);

} catch (Exception $e) {
    echo json_encode(["status" => false, "message" => "Error: " . $e->getMessage()]);
} catch (Throwable $e) {
    echo json_encode(["status" => false, "message" => "Critical Error: " . $e->getMessage()]);
}
