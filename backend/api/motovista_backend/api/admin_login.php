<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';

$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, TRUE);

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($input['email'], $input['password'])) {
        $user_input = $input['email'];
        $password = $input['password'];

        try {
            // Binding the input twice to check both columns
            $stmt = $conn->prepare("SELECT * FROM admins WHERE email = ? OR username = ? LIMIT 1");
            $stmt->execute([$user_input, $user_input]);
            $admin = $stmt->fetch(PDO::FETCH_ASSOC);

            if ($admin) {
                // password_verify handles the comparison with the hash automatically
                if (password_verify($password, $admin['password'])) {
                    $token = bin2hex(random_bytes(32));

                    $update = $conn->prepare("UPDATE admins SET token = ? WHERE id = ?");
                    $update->execute([$token, $admin['id']]);

                    echo json_encode([
                        "success" => true,
                        "message" => "Admin Login Successful",
                        "data" => [
                            "token" => $token,
                            "customer" => [
                                "id" => $admin['id'],
                                "full_name" => "Administrator",
                                "email" => $admin['email'],
                                "workflow_stage" => $admin['workflow_stage'] ?? null,
                                "active_order_id" => $admin['active_order_id'] ?? null
                            ]
                        ]
                    ]);
                } else {
                    echo json_encode(["success" => false, "message" => "Incorrect password."]);
                }
            } else {
                echo json_encode(["success" => false, "message" => "Admin account not found."]);
            }
        } catch (PDOException $e) {
            echo json_encode(["success" => false, "message" => "Database error: " . $e->getMessage()]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "Missing email or password."]);
    }
}
?>