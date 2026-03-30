<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';

$input = json_decode(file_get_contents('php://input'), true);
$headers = apache_request_headers();
$token = str_replace('Bearer ', '', $headers['Authorization'] ?? '');

if (!$token) {
    echo json_encode(["status" => false, "message" => "Unauthorized"]);
    exit;
}

$name = $input['full_name'];
$email = $input['email'];
$phone = $input['phone'];

try {
    $stmt = $conn->prepare("UPDATE users SET full_name = ?, email = ?, phone = ? WHERE token = ?");
    if ($stmt->execute([$name, $email, $phone, $token])) {
        echo json_encode(["status" => true, "message" => "Profile updated"]);
    } else {
        echo json_encode(["status" => false, "message" => "Update failed"]);
    }
} catch (PDOException $e) {
    echo json_encode(["status" => false, "message" => $e->getMessage()]);
}
?>