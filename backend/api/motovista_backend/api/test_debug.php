<?php
header("Content-Type: text/plain");
ini_set('display_errors', 1);
error_reporting(E_ALL);

echo "1. Testing DB Connection...\n";
require_once '../config/db_connect.php';
if (isset($conn) && $conn) {
    echo "   [OK] DB Connected\n";
} else {
    echo "   [FAIL] DB Connection failed\n";
}

echo "2. Loading Email Config...\n";
require_once '../config/email_config.php';
echo "   [OK] Loaded. Host: " . SMTP_HOST . "\n";

echo "3. Loading PHPMailer...\n";
require_once '../libs/PHPMailer/Exception.php';
require_once '../libs/PHPMailer/PHPMailer.php';
require_once '../libs/PHPMailer/SMTP.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

if (class_exists('PHPMailer\PHPMailer\PHPMailer')) {
    echo "   [OK] PHPMailer Class Found\n";
} else {
    echo "   [FAIL] PHPMailer Class NOT Found\n";
    exit;
}

echo "4. Testing Email Sending (to configured username)...\n";
$mail = new PHPMailer(true);
try {
    $mail->isSMTP();
    $mail->Host = SMTP_HOST;
    $mail->SMTPAuth = true;
    $mail->Username = SMTP_USERNAME;
    $mail->Password = SMTP_PASSWORD;
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port = SMTP_PORT;

    $mail->setFrom(SMTP_FROM_EMAIL, SMTP_FROM_NAME);
    $mail->addAddress(SMTP_USERNAME); // Send to self

    $mail->isHTML(false);
    $mail->Subject = "Test Email from Debug Script";
    $mail->Body = "If you see this, email config is working.";

    $mail->send();
    echo "   [OK] Email Sent Successfully!\n";
} catch (Exception $e) {
    echo "   [FAIL] Email Failed: " . $mail->ErrorInfo . "\n";
}
?>