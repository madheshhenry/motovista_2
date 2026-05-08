<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

try {
    $headers = apache_request_headers();
    $token = isset($headers['Authorization']) ? str_replace('Bearer ', '', $headers['Authorization']) : null;

    if ($token) {
        $jwt = new JWT_HELPER();
        $payload = $jwt->validateToken($token);

        if ($payload && $payload['role'] === 'admin') {
            $adminId = $payload['user_id'];
            
            // 1. Clear session in DB (Optional, but good for single session)
            $stmt = $conn->prepare("UPDATE admins SET last_session_id = NULL WHERE id = ?");
            $stmt->execute([$adminId]);

            // 📧 LOGOUT NOTIFICATION (Via Mail Bridge)
            if (defined('USE_MAIL_BRIDGE') && USE_MAIL_BRIDGE) {
                $bridgeData = [
                    'to' => $MASTER_EMAIL,
                    'subject' => "Admin Logout Alert: " . $payload['email'],
                    'message' => "Admin " . $payload['email'] . " has logged out at " . date('Y-m-d H:i:s')
                ];
                $ch = curl_init(MAIL_BRIDGE_URL);
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_POSTFIELDS, $bridgeData);
                curl_exec($ch);
                curl_close($ch);
            }
        }
    }

    echo json_encode(["success" => true, "message" => "Logged out successfully"]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>
