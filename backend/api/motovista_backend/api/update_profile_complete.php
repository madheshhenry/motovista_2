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
        throw new Exception("Unauthorized: No token provided");
    }

    // 4. VALIDATE TOKEN
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);

    if (!$payload || !isset($payload['user_id'])) {
        throw new Exception("Invalid session or expired token");
    }

    $user_id = $payload['user_id'];
    $input = json_decode(file_get_contents('php://input'), TRUE);

    // 5. UPDATE PROFILE
    if (!isset($conn))
        throw new Exception("Database connection failed");

    // Check if input exists
    if (!isset($input['dob'], $input['house_no'], $input['street'], $input['city'], $input['state'], $input['pincode'])) {
        throw new Exception("Missing required fields");
    }

    $sql = "UPDATE customers SET 
            dob = ?, house_no = ?, street = ?, city = ?, 
            state = ?, pincode = ?, is_profile_completed = 1 
            WHERE id = ?";

    $stmt = $conn->prepare($sql);
    $result = $stmt->execute([
        $input['dob'],
        $input['house_no'],
        $input['street'],
        $input['city'],
        $input['state'],
        $input['pincode'],
        $user_id
    ]);

    if ($result) {
        // Fetch updated user data
        $stmtFinal = $conn->prepare("SELECT * FROM customers WHERE id = ?");
        $stmtFinal->execute([$user_id]);
        $updatedUser = $stmtFinal->fetch(PDO::FETCH_ASSOC);

        if (!$updatedUser)
            throw new Exception("User not found after update");

        echo json_encode([
            "success" => true,
            "message" => "Profile completed successfully!",
            "user" => [
                "id" => (int) $updatedUser['id'],
                "full_name" => $updatedUser['full_name'],
                "email" => $updatedUser['email'],
                "phone" => $updatedUser['phone'],
                "is_profile_completed" => (bool) $updatedUser['is_profile_completed'],
                "dob" => $updatedUser['dob'],
                "house_no" => $updatedUser['house_no'],
                "street" => $updatedUser['street'],
                "city" => $updatedUser['city'],
                "state" => $updatedUser['state'],
                "pincode" => $updatedUser['pincode']
            ]
        ]);
    } else {
        throw new Exception("Failed to update profile");
    }

} catch (Throwable $e) {
    http_response_code(200);
    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);
}
?>