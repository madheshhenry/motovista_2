<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

$input = json_decode(file_get_contents('php://input'), TRUE);

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = $input['email'] ?? '';
    $password = $input['password'] ?? '';

    if (empty($email) || empty($password)) {
        echo json_encode(["success" => false, "message" => "Email and password are required."]);
        exit;
    }

    try {
        // Use 'customers' table instead of 'users'
        $stmt = $conn->prepare("SELECT * FROM customers WHERE email = ?");
        $stmt->execute([$email]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($user && password_verify($password, $user['password'])) {

            // ✅ CHECK VERIFICATION STATUS (using 'email_verified' column)
            if ($user['email_verified'] == 0) {
                echo json_encode([
                    "success" => true, // Success true to allow Android to show verify message
                    "message" => "Please verify your email first.",
                    "data" => [
                        "requires_verification" => true,
                        "token" => null,
                        "customer" => null
                    ]
                ]);
                exit;
            }

            // Generate JWT Token
            $jwt = new JWT_HELPER();
            $token = $jwt->generateToken($user['id'], $user['email']);

            // ✅ Matches your LoginResponse.java structure
            echo json_encode([
                "success" => true,
                "message" => "Login successful!",
                "data" => [
                    "token" => $token,
                    "requires_verification" => false,
                    "customer" => [
                        "id" => (int) $user['id'],
                        "full_name" => $user['full_name'],
                        "email" => $user['email'],
                        "phone" => $user['phone'],
                        // Check if profile is completed (logic can be improved based on specific fields)
                        "is_profile_completed" => !empty($user['house_no']),
                        "created_at" => $user['created_at']
                    ]
                ]
            ]);
        } else {
            http_response_code(401);
            echo json_encode(["success" => false, "message" => "Invalid email or password."]);
        }
    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode(["success" => false, "message" => "Server error: " . $e->getMessage()]);
    }
}
?>