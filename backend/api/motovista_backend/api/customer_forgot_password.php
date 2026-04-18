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

    // 1. Check if customer exists
    $stmt = $conn->prepare("SELECT id FROM customers WHERE email = ?");
    $stmt->execute([$email]);
    $customer = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$customer) {
        throw new Exception("No account found with this email address");
    }

    // 2. Generate OTP
    $otp = rand(100000, 999999);
    // 3. Save OTP to DB using MySQL time to avoid timezone issues
    $updateStmt = $conn->prepare("UPDATE customers SET otp = ?, otp_expiry = DATE_ADD(NOW(), INTERVAL 10 MINUTE) WHERE id = ?");
    $updateStmt->execute([$otp, $customer['id']]);

    // 4. Send Email
    $subject = 'Password Reset OTP - MotoVista';
    $email_body = "
        <div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eee;'>
            <h2 style='color: #2563eb;'>Password Reset Request</h2>
            <p>Hello,</p>
            <p>You requested to reset your password. Use the code below to proceed:</p>
            <div style='background: #f3f4f6; padding: 15px; text-align: center; border-radius: 8px; margin: 20px 0;'>
                <span style='font-size: 24px; font-weight: bold; letter-spacing: 5px; color: #1f2937;'>$otp</span>
            </div>
            <p>This code is valid for <strong>10 minutes</strong>. If you didn't request this, please ignore this email.</p>
            <p>Best regards,<br>The MotoVista Team</p>
        </div>
    ";

    $email_sent = false;

    // --- OPTION A: WEB BRIDGE (Bypass blocked ports) ---
    if (defined('USE_MAIL_BRIDGE') && USE_MAIL_BRIDGE) {
        try {
            $ch = curl_init(MAIL_BRIDGE_URL);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode([
                'to' => $email,
                'subject' => $subject,
                'body' => $email_body
            ]));
            curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
            curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
            curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
            curl_setopt($ch, CURLOPT_TIMEOUT, 15);
            
            $bridge_res = curl_exec($ch);
            $res_data = json_decode($bridge_res, true);
            curl_close($ch);
            
            if ($res_data && isset($res_data['success']) && $res_data['success']) {
                $email_sent = true;
            }
        } catch (Exception $e) { /* Fallback to SMTP */ }
    }

    // --- OPTION B: STANDARD SMTP (PHPMailer) ---
    if (!$email_sent) {
        try {
            $mail = new PHPMailer(true);
            $mail->isSMTP();
            $mail->Host = SMTP_HOST;
            $mail->SMTPAuth = true;
            $mail->Username = SMTP_USERNAME;
            $mail->Password = SMTP_PASSWORD;
            $mail->SMTPSecure = PHPMailer::ENCRYPTION_SMTPS;
            $mail->Port = SMTP_PORT;
            $mail->SMTPOptions = array('ssl'=>array('verify_peer'=>false, 'verify_peer_name'=>false, 'allow_self_signed'=>true));

            $mail->setFrom(SMTP_USERNAME, 'MotoVista Support');
            $mail->addAddress($email);
            $mail->isHTML(true);
            $mail->Subject = $subject;
            $mail->Body = $email_body;

            if ($mail->send()) {
                $email_sent = true;
            }
        } catch (Exception $e) { /* Fallback to File Log */ }
    }

    if ($email_sent) {
        echo json_encode(["success" => true, "message" => "Verification code sent to your email"]);
    } else {
        // --- OPTION C: FILE LOG FALLBACK ---
        $log_entry = date('Y-m-d H:i:s') . " - Forgot Pass OTP for $email: $otp\n";
        file_put_contents('mail_log.txt', $log_entry, FILE_APPEND);
        echo json_encode(["success" => true, "message" => "Verification code generated! [Dev Mode: Check mail_log.txt]"]);
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>