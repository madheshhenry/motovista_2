<?php
// 1. DISABLE RAW OUTPUT
ini_set('display_errors', 0);
ini_set('log_errors', 1);
ini_set('error_log', '../php_error.log');
header("Content-Type: application/json; charset=UTF-8");

try {
    // 2. INCLUDE FILES
    if (!file_exists('../config/db_connect.php'))
        throw new Exception("Config file db_connect.php not found");
    require_once '../config/db_connect.php';

    if (!file_exists('../config/jwt_helper.php'))
        throw new Exception("Config file jwt_helper.php not found");
    require_once '../config/jwt_helper.php';

    // 3. GET BEARER TOKEN
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';

    // Support "Bearer <token>" format
    if (preg_match('/Bearer\s(\S+)/', $authHeader, $matches)) {
        $token = $matches[1];
    } else {
        $token = $authHeader; // Fallback
    }

    if (empty($token)) {
        echo json_encode(["success" => false, "message" => "Unauthorized: No token provided"]);
        exit;
    }

    // 4. VALIDATE TOKEN
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);

    if (!$payload || !isset($payload['user_id'])) {
        echo json_encode(["success" => false, "message" => "Invalid session or expired token"]);
        exit;
    }

    $user_id = $payload['user_id'];

    if (!isset($conn))
        throw new Exception("Database connection failed");

    // 5. FETCH USER
    // Ensure we select all fields expected by the Android App Data class
    $stmt = $conn->prepare("SELECT id, full_name, email, phone, profile_image FROM customers WHERE id = ? LIMIT 1");
    $stmt->execute([$user_id]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($user) {
        // If profile_image is null, ensure we return it as null or empty string if preferred, 
        // but JSON null is fine for Java String.
        $profileImage = $user['profile_image'];
        if ($profileImage && strpos($profileImage, 'uploads/') === false) {
            $profileImage = 'uploads/profile_pics/' . $profileImage;
        }

        echo json_encode([
            "success" => true,
            "data" => [
                "id" => (int) $user['id'],
                "full_name" => $user['full_name'],
                "email" => $user['email'],
                "phone" => $user['phone'],
                "profile_image" => $profileImage
            ]
        ]);
    } else {
        echo json_encode(["success" => false, "message" => "User record not found in database"]);
    }

} catch (Throwable $e) {
    http_response_code(200);
    echo json_encode([
        "success" => false,
        "message" => "Server Error: " . $e->getMessage()
    ]);
}
?>