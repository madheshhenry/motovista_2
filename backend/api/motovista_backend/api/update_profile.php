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

    // 5. HANDLE IMAGE UPLOAD
    $profile_image_name = null;
    $aadhar_front_name = null;
    $aadhar_back_name = null;

    $profile_dir = "../uploads/profile_pics/";
    $aadhar_dir = "../uploads/aadhar/";

    if (!file_exists($profile_dir))
        mkdir($profile_dir, 0777, true);
    if (!file_exists($aadhar_dir))
        mkdir($aadhar_dir, 0777, true);

    $allowed_exts = ['jpg', 'jpeg', 'png', 'gif', 'webp'];

    // Helper function to handle upload
    function handleUpload($fileKey, $targetDir, $userId, $prefix, $allowed_exts)
    {
        if (isset($_FILES[$fileKey]) && $_FILES[$fileKey]['error'] == 0) {
            $file_ext = strtolower(pathinfo($_FILES[$fileKey]["name"], PATHINFO_EXTENSION));
            if (in_array($file_ext, $allowed_exts)) {
                $new_filename = $prefix . "_" . $userId . "_" . time() . "." . $file_ext;
                if (move_uploaded_file($_FILES[$fileKey]["tmp_name"], $targetDir . $new_filename)) {
                    return $new_filename;
                }
            }
        }
        return null; // Return null if active upload failed
    }

    $profile_image_name = handleUpload('profile_image', $profile_dir, $user_id, "profile", $allowed_exts);
    $aadhar_front_name = handleUpload('aadhar_front', $aadhar_dir, $user_id, "aadhar_front", $allowed_exts);
    $aadhar_back_name = handleUpload('aadhar_back', $aadhar_dir, $user_id, "aadhar_back", $allowed_exts);

    // 6. UPDATE DATABASE (Dynamic SQL)
    // We only update fields that are ACTUALLY provided in the request.

    // Potential fields to update
    $fieldsToUpdate = [];
    $params = [];

    // Mapping POST keys to DB columns
    $updatableFields = [
        'full_name',
        'email',
        'phone',
        'dob',
        'house_no',
        'street',
        'city',
        'state',
        'pincode',
        'pan_no'
    ];

    foreach ($updatableFields as $field) {
        if (isset($_POST[$field])) {
            $val = trim($_POST[$field]);
            // If it's an address field and empty, we might want to allow it or skip it.
            // For now, let's update it if sent.
            $fieldsToUpdate[] = "$field = ?";
            $params[] = $val;
        }
    }

    // Always set is_profile_completed = 1 if we are saving address info
    // (Optional: logic to decide when to set this true, but usually safe to set 1 if updating)
    // Let's only force it if 'dob' is present (implying full profile setup)
    if (isset($_POST['dob'])) {
        $fieldsToUpdate[] = "is_profile_completed = 1";
    }

    if ($profile_image_name) {
        $fieldsToUpdate[] = "profile_image = ?";
        $params[] = $profile_image_name;
    }

    if ($aadhar_front_name) {
        $fieldsToUpdate[] = "aadhar_front = ?";
        $params[] = $aadhar_front_name;
    }

    if ($aadhar_back_name) {
        $fieldsToUpdate[] = "aadhar_back = ?";
        $params[] = $aadhar_back_name;
    }

    if (empty($fieldsToUpdate)) {
        echo json_encode(["success" => false, "message" => "No changes provided"]);
        exit;
    }

    $sql = "UPDATE customers SET " . implode(", ", $fieldsToUpdate) . " WHERE id = ?";
    $params[] = $user_id;

    $stmt = $conn->prepare($sql);
    if ($stmt->execute($params)) {

        // Fetch updated user to return
        $stmtUser = $conn->prepare("SELECT * FROM customers WHERE id = ?");
        $stmtUser->execute([$user_id]);
        $updatedUser = $stmtUser->fetch(PDO::FETCH_ASSOC);

        echo json_encode([
            "success" => true,
            "message" => "Profile updated successfully!",
            "user" => [
                "id" => (int) $updatedUser['id'],
                "full_name" => $updatedUser['full_name'],
                "email" => $updatedUser['email'],
                "phone" => $updatedUser['phone'],
                "profile_image" => $updatedUser['profile_image'],
                "is_profile_completed" => (bool) $updatedUser['is_profile_completed']
            ]
        ]);
    } else {
        echo json_encode(["success" => false, "message" => "Database update failed"]);
    }

} catch (Throwable $e) {
    http_response_code(200);
    echo json_encode([
        "success" => false,
        "message" => "Server Error: " . $e->getMessage()
    ]);
}
?>