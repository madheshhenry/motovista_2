<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';
require_once '../config/email_config.php';

// PHPMailer Namespace Setup
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require_once '../libs/PHPMailer/Exception.php';
require_once '../libs/PHPMailer/PHPMailer.php';
require_once '../libs/PHPMailer/SMTP.php';

$input = json_decode(file_get_contents('php://input'), TRUE);

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_token = $input['id_token'] ?? '';

    if (empty($id_token)) {
        echo json_encode(["success" => false, "message" => "ID Token is required."]);
        exit;
    }

    try {
        // 1. Verify Token with Google (Using CURL for better reliability on Windows/XAMPP)
        $url = "https://oauth2.googleapis.com/tokeninfo?id_token=" . $id_token;
        
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false); // Bypass SSL for local dev environments
        curl_setopt($ch, CURLOPT_TIMEOUT, 10);
        
        $response = curl_exec($ch);
        $http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $curl_error = curl_error($ch);
        curl_close($ch);

        if ($response === false) {
            throw new Exception("Google connection failed: " . $curl_error);
        }

        $payload = json_decode($response, true);

        if (!$payload || isset($payload['error_description']) || $http_code !== 200) {
            $msg = $payload['error_description'] ?? "Invalid Google Token";
            echo json_encode(["success" => false, "message" => $msg]);
            exit;
        }

        $email = $payload['email'];
        $full_name = $payload['name'] ?? 'Google User';
        $google_id = $payload['sub']; 
        $profile_pic = $payload['picture'] ?? null;

        // 2. Check if user already exists
        $stmt = $conn->prepare("SELECT * FROM customers WHERE email = ?");
        $stmt->execute([$email]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$user) {
            // New user, auto-register
            $dummy_phone = "G" . substr(time(), -10) . rand(10, 99); 
            $random_password = password_hash(bin2hex(random_bytes(16)), PASSWORD_DEFAULT);
            
            $insert = $conn->prepare("INSERT INTO customers (full_name, email, phone, password, status, email_verified, email_verified_at, profile_image, is_profile_completed, created_at) VALUES (?, ?, ?, ?, 'active', 1, NOW(), ?, 0, NOW())");
            
            if ($insert->execute([$full_name, $email, $dummy_phone, $random_password, $profile_pic])) {
                $user_id = $conn->lastInsertId();
                $stmt = $conn->prepare("SELECT * FROM customers WHERE id = ?");
                $stmt->execute([$user_id]);
                $user = $stmt->fetch(PDO::FETCH_ASSOC);
            } else {
                throw new Exception("Failed to create customer account in database.");
            }
        }

        // 3. Generate OTP
        $otp = rand(100000, 999999);
        $updateStmt = $conn->prepare("UPDATE customers SET otp = ?, otp_expiry = DATE_ADD(NOW(), INTERVAL 10 MINUTE) WHERE id = ?");
        if (!$updateStmt->execute([$otp, $user['id']])) {
            throw new Exception("Database failed to save verification code.");
        }

        // 4. Send Email with OTP
        $subject = 'Google Login Verification - MotoVista';
        $email_body = "
            <div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #13A4EC;'>
                <h2 style='color: #13A4EC;'>Google Account Verification</h2>
                <p>Hello $full_name,</p>
                <p>To complete your login to MotoVista using Google, please use the verification code below:</p>
                <div style='background: #f0f9ff; padding: 15px; text-align: center; border-radius: 12px; margin: 20px 0;'>
                    <span style='font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #0C4A6E;'>$otp</span>
                </div>
                <p>This code will expire in 10 minutes.</p>
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
            } catch (Exception $e) { /* Use file log fallback */ }
        }

        // --- OPTION C: FILE LOG FALLBACK ---
        $debug_msg = "";
        if (!$email_sent) {
            $log_entry = date('Y-m-d H:i:s') . " - OTP for $email: $otp\n";
            file_put_contents('mail_log.txt', $log_entry, FILE_APPEND);
            $debug_msg = " [Dev Mode: Check mail_log.txt]";
        }

        // 5. Response
        echo json_encode([
            "success" => true,
            "message" => ($email_sent ? "Verification code sent to your Google email." : "Verification code generated! $debug_msg"),
            "data" => [
                "email" => $email,
                "requires_verification" => true
            ]
        ]);

    } catch (Exception $e) {
        // Return 200 but with success: false so app can show the specific error message
        echo json_encode(["success" => false, "message" => "Server error: " . $e->getMessage()]);
    }
}
?>
