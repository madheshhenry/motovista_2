<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';

$response = array();

// Get the Token from the Authorization Header
$headers = apache_request_headers();
$auth_header = isset($headers['Authorization']) ? $headers['Authorization'] : '';
$token = str_replace('Bearer ', '', $auth_header);

if (!empty($token)) {
    try {
        // Find user by this token
        $stmt = $conn->prepare("SELECT id, full_name, email, is_verified FROM users WHERE token = ? LIMIT 1");
        $stmt->execute([$token]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($user) {
            $response['success'] = true;
            $response['message'] = "Session Valid";
            $response['user'] = $user;
        } else {
            http_response_code(401);
            $response['success'] = false;
            $response['message'] = "Session Expired. Please login again.";
        }
    } catch (PDOException $e) {
        $response['success'] = false;
        $response['message'] = "Error: " . $e->getMessage();
    }
} else {
    http_response_code(400);
    $response['success'] = false;
    $response['message'] = "Token missing.";
}

echo json_encode($response);
?>