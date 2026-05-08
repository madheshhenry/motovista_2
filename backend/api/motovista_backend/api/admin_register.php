<?php
header("Content-Type: application/json; charset=UTF-8");
ini_set('display_errors', 0);

require_once '../config/db_connect.php';

// 👑 MASTER CONFIG
$MASTER_EMAIL = "master_admin@motovista.com"; // 🚨 Change this to your real email
$MASTER_KEY = "MOTO_ADMIN_2024";

try {
    $data = json_decode(file_get_contents("php://input"));

    if (!isset($data->email) || !isset($data->password) || !isset($data->username) || !isset($data->master_key)) {
        echo json_encode(["success" => false, "message" => "Missing required fields"]);
        exit;
    }

    if ($data->master_key !== $MASTER_KEY) {
        echo json_encode(["success" => false, "message" => "Invalid Secret Key!"]);
        exit;
    }

    $email = trim($data->email);
    $username = trim($data->username);
    $password = password_hash($data->password, PASSWORD_DEFAULT);

    $stmt = $conn->prepare("SELECT id FROM admins WHERE email = ? OR username = ?");
    $stmt->execute([$email, $username]);
    if ($stmt->fetch()) {
        echo json_encode(["success" => false, "message" => "Admin already exists"]);
        exit;
    }

    // Insert as PENDING (is_approved = 0)
    $stmt = $conn->prepare("INSERT INTO admins (username, email, password, is_approved) VALUES (?, ?, ?, 0)");
        // 📧 NOTIFY MASTER ADMIN (Via Mail Bridge)
        if (defined('USE_MAIL_BRIDGE') && USE_MAIL_BRIDGE) {
            $bridgeData = [
                'to' => $MASTER_EMAIL,
                'subject' => "New Admin Registration Request: $username",
                'message' => "A new sub-admin has registered and is waiting for your approval.\n\nUsername: $username\nEmail: $email"
            ];
            $ch = curl_init(MAIL_BRIDGE_URL);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_POSTFIELDS, $bridgeData);
            curl_exec($ch);
            curl_close($ch);
        }

        echo json_encode(["success" => true, "message" => "Registration successful! Waiting for Master Admin approval."]);
    } else {
        echo json_encode(["success" => false, "message" => "Registration failed"]);
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => "Server Error: " . $e->getMessage()]);
}
?>
