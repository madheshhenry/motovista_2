<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!isset($data['email']) || !isset($data['otp'])) {
        throw new Exception("Email and verification code are required");
    }

    $email = trim($data['email']);
    $otp = trim($data['otp']);

    $stmt = $conn->prepare("SELECT id FROM customers WHERE email = ? AND otp = ? AND otp_expiry > NOW()");
    $stmt->execute([$email, $otp]);
    $customer = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$customer) {
        throw new Exception("Invalid or expired verification code");
    }

    echo json_encode(["success" => true, "message" => "Code verified successfully"]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>