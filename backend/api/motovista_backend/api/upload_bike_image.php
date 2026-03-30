<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

// ENABLE ERROR REPORTING FOR DEBUGGING
ini_set('display_errors', 1);
error_reporting(E_ALL);

// LOGGING FUNCTION
function logDebug($message)
{
    file_put_contents('upload_debug.txt', date('[Y-m-d H:i:s] ') . $message . PHP_EOL, FILE_APPEND);
}

logDebug("Script started.");

try {
    // 1. Authorization
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';

    if (!$authHeader) {
        logDebug("Auth failed: No token");
        throw new Exception("Authorization token missing");
    }

    $token = str_replace('Bearer ', '', $authHeader);
    $payload = (new JWT_HELPER())->validateToken($token);

    if (!$payload || !isset($payload['role']) || $payload['role'] !== 'admin') {
        logDebug("Auth failed: Invalid role or token");
        throw new Exception("Unauthorized access");
    }

    // 2. Check for Upload
    if (!isset($_FILES['bike_images'])) {
        logDebug("No 'bike_images' key in \$_FILES. Keys found: " . print_r(array_keys($_FILES), true));
        throw new Exception("No images received (key 'bike_images' missing)");
    }

    // 3. Setup Directory
    $uploadDir = '../uploads/bikes/';
    // Absolute path check
    $absoluteDir = realpath('../') . '/uploads/bikes/';
    logDebug("Target Directory: " . $absoluteDir);

    if (!file_exists($uploadDir)) {
        logDebug("Directory did not exist, attempting creation.");
        if (!mkdir($uploadDir, 0777, true)) {
            logDebug("Failed to create directory.");
            throw new Exception("Failed to create upload directory");
        }
    }

    // 4. Process Files
    $uploadedPaths = [];
    $files = $_FILES['bike_images'];

    // Handle single or multiple file uploads
    $fileCount = is_array($files['name']) ? count($files['name']) : 1;
    logDebug("Processing $fileCount file(s).");

    for ($i = 0; $i < $fileCount; $i++) {
        $name = is_array($files['name']) ? $files['name'][$i] : $files['name'];
        $tmpName = is_array($files['tmp_name']) ? $files['tmp_name'][$i] : $files['tmp_name'];
        $error = is_array($files['error']) ? $files['error'][$i] : $files['error'];

        logDebug("File $i: Name=$name, Tmp=$tmpName, Error=$error");

        if ($error === UPLOAD_ERR_OK) {
            $extension = pathinfo($name, PATHINFO_EXTENSION);
            if (!$extension)
                $extension = 'jpg'; // Default to jpg if missing

            $newFileName = uniqid('bike_') . '.' . $extension;
            $targetPath = $uploadDir . $newFileName;

            if (move_uploaded_file($tmpName, $targetPath)) {
                logDebug("SUCCESS: Moved to $targetPath");
                $uploadedPaths[] = 'uploads/bikes/' . $newFileName; // Relative path for DB
            } else {
                logDebug("FAIL: move_uploaded_file returned false.");
                throw new Exception("Failed to save file: " . $name);
            }
        } else {
            logDebug("Upload Error Code: $error");
            throw new Exception("Upload error code: " . $error);
        }
    }

    if (empty($uploadedPaths)) {
        throw new Exception("No files were successfully uploaded.");
    }

    $response = [
        "status" => "success",
        "message" => "Images uploaded successfully",
        "data" => $uploadedPaths // Returns ["uploads/bikes/bike_xxx.jpg"]
    ];

    logDebug("Sending success response: " . json_encode($response));
    echo json_encode($response);

} catch (Exception $e) {
    $errorMsg = $e->getMessage();
    logDebug("EXCEPTION: " . $errorMsg);
    http_response_code(400);
    echo json_encode([
        "status" => "error",
        "message" => $errorMsg
    ]);
}
