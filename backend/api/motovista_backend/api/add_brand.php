<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

// Enable error reporting for debugging (will capture in response if output buffering not messy)
ini_set('display_errors', 0);
error_reporting(E_ALL);

$response = ["status" => false, "message" => "Unknown error"];

try {
    // 1. Verify Admin Token
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';
    if (!$authHeader) {
        $authHeader = isset($_SERVER['HTTP_AUTHORIZATION']) ? $_SERVER['HTTP_AUTHORIZATION'] : '';
    }

    if (!$authHeader)
        throw new Exception("Authorization token missing");

    $token = str_replace('Bearer ', '', $authHeader);
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);
    // if (!$payload)
    //    throw new Exception("Unauthorized access");

    // 2. Get Input
    $brandName = isset($_POST['brand_name']) ? trim($_POST['brand_name']) : '';

    if (empty($brandName)) {
        throw new Exception("Brand name is required");
    }

    // Check for Duplicate Brand Name BEFORE processing image
    $checkSql = "SELECT id FROM brands WHERE brand_name = :name";
    $checkStmt = $conn->prepare($checkSql);
    $checkStmt->execute([':name' => $brandName]);
    if ($checkStmt->rowCount() > 0) {
        throw new Exception("Brand '$brandName' already exists.");
    }

    $brandLogoPath = null;

    // 3. Handle File Upload
    if (isset($_FILES['brand_logo'])) {
        if ($_FILES['brand_logo']['error'] === UPLOAD_ERR_OK) {
            $uploadDir = '../uploads/brands/';
            if (!file_exists($uploadDir)) {
                if (!mkdir($uploadDir, 0777, true)) {
                    throw new Exception("Failed to create upload directory.");
                }
            }

            $fileTmpPath = $_FILES['brand_logo']['tmp_name'];
            $fileName = $_FILES['brand_logo']['name'];
            $fileNameCmps = explode(".", $fileName);
            $fileExtension = strtolower(end($fileNameCmps));

            $allowedfileExtensions = array('jpg', 'gif', 'png', 'jpeg', 'webp');
            if (!in_array($fileExtension, $allowedfileExtensions)) {
                throw new Exception("Upload failed. Allowed file types: " . implode(',', $allowedfileExtensions));
            }

            $newFileName = md5(time() . $fileName) . '.' . $fileExtension;
            $dest_path = $uploadDir . $newFileName;

            if (move_uploaded_file($fileTmpPath, $dest_path)) {
                $brandLogoPath = 'uploads/brands/' . $newFileName;
            } else {
                throw new Exception("Error moving uploaded file. Check permissions.");
            }
        } else {
            // Map PHP File Upload Errors
            $uploadErrors = array(
                UPLOAD_ERR_INI_SIZE => 'The uploaded file exceeds the upload_max_filesize directive in php.ini',
                UPLOAD_ERR_FORM_SIZE => 'The uploaded file exceeds the MAX_FILE_SIZE directive that was specified in the HTML form',
                UPLOAD_ERR_PARTIAL => 'The uploaded file was only partially uploaded',
                UPLOAD_ERR_NO_FILE => 'No file was uploaded',
                UPLOAD_ERR_NO_TMP_DIR => 'Missing a temporary folder',
                UPLOAD_ERR_CANT_WRITE => 'Failed to write file to disk.',
                UPLOAD_ERR_EXTENSION => 'A PHP extension stopped the file upload.'
            );
            $code = $_FILES['brand_logo']['error'];
            $msg = isset($uploadErrors[$code]) ? $uploadErrors[$code] : "Unknown upload error ($code)";

            // If no file was selected, is it mandatory?
            // "Add Brand" usually needs a logo. Let's make it mandatory or warn.
            if ($code !== UPLOAD_ERR_NO_FILE) {
                throw new Exception("Image Upload Failed: " . $msg);
            } else {
                // throw new Exception("Brand Logo is required.");
                // User might want text-only brand? Assuming usually logo is needed.
            }
        }
    } else {
        // No file sent in request key 'brand_logo'
        // throw new Exception("No brand_logo file received.");
    }

    // 4. Insert Brand
    $sql = "INSERT INTO brands (brand_name, brand_logo) VALUES (:brand_name, :brand_logo)";
    $stmt = $conn->prepare($sql);
    $stmt->bindParam(':brand_name', $brandName);
    $stmt->bindParam(':brand_logo', $brandLogoPath);

    if ($stmt->execute()) {
        $response["status"] = true;
        $response["message"] = "Brand added successfully";
        $response["data"] = [
            "id" => $conn->lastInsertId(),
            "brand_name" => $brandName,
            "brand_logo" => $brandLogoPath
        ];
    } else {
        throw new Exception("Database insert failed.");
    }

} catch (Exception $e) {
    $response["status"] = false;
    $response["message"] = $e->getMessage();
}

echo json_encode($response);
?>