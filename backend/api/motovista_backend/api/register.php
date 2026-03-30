<?php
// 1. DISABLE RAW OUTPUT
ini_set('display_errors', 0);
ini_set('log_errors', 1);
ini_set('error_log', '../php_error.log');
header("Content-Type: application/json; charset=UTF-8");

require_once '../libs/PHPMailer/Exception.php';
require_once '../libs/PHPMailer/PHPMailer.php';
require_once '../libs/PHPMailer/SMTP.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception as MailException;

try {
    // 2. INCLUDE FILES (Check if they exist)
    if (!file_exists('../config/db_connect.php'))
        throw new Exception("Config file db_connect.php not found");
    require_once '../config/db_connect.php';

    if (!file_exists('../config/jwt_helper.php'))
        throw new Exception("Config file jwt_helper.php not found");
    require_once '../config/jwt_helper.php';

    if (!file_exists('../config/email_config.php'))
        throw new Exception("Config file email_config.php not found");
    require_once '../config/email_config.php';

    // 3. CHECK DB CONNECTION
    if (!isset($conn)) {
        throw new Exception("Database connection failed (variable not set). Check db_connect.php");
    }

    $input = json_decode(file_get_contents('php://input'), TRUE);

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        // Basic validation
        if (!isset($input['full_name'], $input['email'], $input['phone'], $input['password'])) {
            throw new Exception('Missing required fields: full_name, email, phone, or password');
        }

        $full_name = trim($input['full_name']);
        $email = trim($input['email']);
        $phone = trim($input['phone']);
        $password = password_hash($input['password'], PASSWORD_DEFAULT);

        // Verification Token
        $verification_token = bin2hex(random_bytes(32));

        // Check if email already exists
        $check = $conn->prepare("SELECT id FROM customers WHERE email = ?");
        $check->execute([$email]);
        if ($check->rowCount() > 0) {
            echo json_encode(["success" => false, "message" => "Email already registered"]);
            exit;
        }

        // Insert into customers table
        $sql = "INSERT INTO customers (full_name, email, phone, password, email_verification_token, status, email_verified, created_at) 
                VALUES (?, ?, ?, ?, ?, 'pending', 0, NOW())";
        $stmt = $conn->prepare($sql);

        if ($stmt->execute([$full_name, $email, $phone, $password, $verification_token])) {
            $user_id = $conn->lastInsertId();

            // Generate JWT Token
            $jwt = new JWT_HELPER();
            $token = $jwt->generateToken($user_id, $email);

            // Email Logic
            $base_url = "http://" . $_SERVER['SERVER_NAME'] . dirname(dirname($_SERVER['SCRIPT_NAME']));
            $verify_link = $base_url . "/web/verify.php?token=" . $verification_token;

            $email_sent = false;
            $email_error = "";
            try {
                $mail = new PHPMailer(true);
                $mail->isSMTP();
                $mail->Host = SMTP_HOST;
                $mail->SMTPAuth = true;
                $mail->Username = SMTP_USERNAME;
                $mail->Password = SMTP_PASSWORD;
                $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
                $mail->Port = SMTP_PORT;

                $mail->setFrom(SMTP_FROM_EMAIL, SMTP_FROM_NAME);
                $mail->addAddress($email, $full_name);

                $mail->isHTML(true);
                $mail->Subject = "Verify Your MotoVista Account";
                $mail->Body = "
                    <h2>Welcome to MotoVista!</h2>
                    <p>Hi $full_name,</p>
                    <p>Thank you for signing up. Please click the link below to verify your email address:</p>
                    <p><a href='$verify_link' style='background-color:#4CAF50; color:white; padding:10px 20px; text-decoration:none; border-radius:5px;'>Verify Email</a></p>
                    <p>Or copy this link: <br> $verify_link</p>
                    <p>Best Regards,<br>MotoVista Team</p>
                ";
                $mail->AltBody = "Hi $full_name, Please verify your email: $verify_link";

                $mail->send();
                $email_sent = true;
            } catch (MailException $e) {
                $email_sent = false;
                $email_error = $mail->ErrorInfo;
                error_log("Mailer Error: " . $email_error);
            } catch (Exception $e) {
                $email_sent = false;
                $email_error = $e->getMessage();
            }

            // Notify Admins about new registration
            try {
                require_once '../includes/FCMManager.php';
                FCMManager::notifyAdmins("New User Registration!", $full_name . " has just signed up.", [
                    "type" => "new_registration",
                    "screen" => "CustomerDetailsActivity",
                    "id" => $user_id,
                    "customer_id" => (int) $user_id
                ]);
            } catch (Exception $fcmError) {
                error_log("Admin FCM Error: " . $fcmError->getMessage());
            }

            echo json_encode([
                "success" => true,
                "message" => "Registration successful! Please verify your email.",
                "data" => [
                    "token" => $token,
                    "requires_verification" => true,
                    "email_sent" => $email_sent,
                    "note" => $email_sent ? "Email sent successfully" : "Email failed: " . $email_error,
                    "customer" => [
                        "id" => (int) $user_id, // Typecast to int
                        "full_name" => $full_name,
                        "email" => $email,
                        "phone" => $phone,
                        "status" => "pending"
                    ]
                ]
            ]);
        } else {
            throw new Exception("Failed to execute insert query.");
        }
    }

} catch (Throwable $e) {
    // 4. CATCH ALL ERRORS (Fatal, PDO, Logic)
    // Return valid JSON even if the server crashes
    http_response_code(200);
    echo json_encode([
        "success" => false,
        "message" => "Server Error: " . $e->getMessage()
    ]);
}
?>