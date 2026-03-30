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

try {
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!isset($data['email'])) {
        throw new Exception("Email is required");
    }

    $email = trim($data['email']);

    // 1. Check if admin exists
    $stmt = $conn->prepare("SELECT id FROM admins WHERE email = ?");
    $stmt->execute([$email]);
    $admin = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$admin) {
        throw new Exception("No administrator account found with this email address");
    }

    // 2. Generate OTP
    $otp = rand(100000, 999999);
    // 3. Save OTP to DB
    $updateStmt = $conn->prepare("UPDATE admins SET otp = ?, otp_expiry = DATE_ADD(NOW(), INTERVAL 10 MINUTE) WHERE id = ?");
    $updateStmt->execute([$otp, $admin['id']]);

    // 4. Send Email
    $mail = new PHPMailer(true);
    $mail->isSMTP();
    $mail->Host = SMTP_HOST;
    $mail->SMTPAuth = true;
    $mail->Username = SMTP_USERNAME;
    $mail->Password = SMTP_PASSWORD;
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port = SMTP_PORT;

    $mail->setFrom(SMTP_USERNAME, 'MotoVista Admin Support');
    $mail->addAddress($email);

    $mail->isHTML(true);
    $mail->Subject = 'Admin Password Reset OTP - MotoVista';
    $mail->Body = "
        <div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eee;'>
            <h2 style='color: #2563eb;'>Admin Password Reset Request</h2>
            <p>Hello Admin,</p>
            <p>You requested to reset your MotoVista dashboard password. Use the security code below to proceed:</p>
            <div style='background: #f3f4f6; padding: 15px; text-align: center; border-radius: 8px; margin: 20px 0;'>
                <span style='font-size: 24px; font-weight: bold; letter-spacing: 5px; color: #1f2937;'>$otp</span>
            </div>
            <p>This code is valid for <strong>10 minutes</strong>. If you didn't request this, please ignore this email or secure your account.</p>
            <p>Best regards,<br>MotoVista Security Team</p>
        </div>
    ";

    $mail->send();

    echo json_encode(["success" => true, "message" => "Admin verification code sent to your email"]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>
