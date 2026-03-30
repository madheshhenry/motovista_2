<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!isset($data['email']) || !isset($data['otp']) || !isset($data['new_password'])) {
        throw new Exception("Missing required fields");
    }

    $email = trim($data['email']);
    $otp = trim($data['otp']);
    $new_password = password_hash(trim($data['new_password']), PASSWORD_BCRYPT);

    // Re-verify OTP for security before resetting
    $stmt = $conn->prepare("SELECT id FROM admins WHERE email = ? AND otp = ? AND otp_expiry > NOW()");
    $stmt->execute([$email, $otp]);
    $admin = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$admin) {
        throw new Exception("Unauthorized admin password reset request");
    }

    // Update password and clear OTP
    $updateStmt = $conn->prepare("UPDATE admins SET password = ?, otp = NULL, otp_expiry = NULL WHERE id = ?");
    $updateStmt->execute([$new_password, $admin['id']]);

    echo json_encode(["success" => true, "message" => "Admin password updated successfully"]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>
