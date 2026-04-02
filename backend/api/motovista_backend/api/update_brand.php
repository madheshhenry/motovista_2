<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

try {
    // 1. Verify Admin Token
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';
    if (!$authHeader)
        throw new Exception("Authorization token missing");

    $token = str_replace('Bearer ', '', $authHeader);
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);
    
    // Check if user is admin (optional depending on your jwt payload structure, but usually validated by token)
    if (!$payload || !isset($payload->user_id))
        throw new Exception("Unauthorized access");

    // Check POST params
    if (!isset($_POST['brand_id']) || !isset($_POST['brand_name'])) {
        throw new Exception("Brand ID and Brand Name are required");
    }

    $brand_id = (int)$_POST['brand_id'];
    $new_brand_name = trim($_POST['brand_name']);

    if (empty($new_brand_name)) {
        throw new Exception("Brand Name cannot be empty");
    }

    // 2. Fetch current brand details to see if name changed
    $stmt = $conn->prepare("SELECT brand_name, brand_logo FROM brands WHERE id = ?");
    $stmt->execute([$brand_id]);
    $current_brand = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$current_brand) {
        throw new Exception("Brand not found");
    }

    $old_brand_name = $current_brand['brand_name'];
    $logo_url = $current_brand['brand_logo'];

    // 3. Handle Image Upload if provided
    if (isset($_FILES['brand_logo']) && $_FILES['brand_logo']['error'] === UPLOAD_ERR_OK) {
        $upload_dir = '../uploads/brands/';
        if (!file_exists($upload_dir)) {
            mkdir($upload_dir, 0777, true);
        }

        $file_extension = strtolower(pathinfo($_FILES['brand_logo']['name'], PATHINFO_EXTENSION));
        $allowed_extensions = ['jpg', 'jpeg', 'png', 'webp'];

        if (!in_array($file_extension, $allowed_extensions)) {
            throw new Exception("Invalid image format. Only JPG, PNG and WEBP are allowed.");
        }

        $new_file_name = uniqid('brand_') . '.' . $file_extension;
        $target_file = $upload_dir . $new_file_name;

        if (move_uploaded_file($_FILES['brand_logo']['tmp_name'], $target_file)) {
            // Delete old file if it exists and is local
            if (!empty($logo_url) && strpos($logo_url, 'uploads/brands/') !== false) {
                // Extract relative path to delete
                $old_file_path = '../' . substr($logo_url, strpos($logo_url, 'uploads/brands/'));
                if (file_exists($old_file_path)) {
                    unlink($old_file_path);
                }
            }
            // You can store full URL or relative. add_brand.php probably stores relative or full URL. Usually we store just the path or file name.
            // Looking at add_brand.php, it probably stores "uploads/brands/filename"
            $logo_url = 'uploads/brands/' . $new_file_name;
        } else {
            throw new Exception("Failed to upload image");
        }
    }

    // 4. Update Database
    $conn->beginTransaction();

    // Update brands table
    $updateStmt = $conn->prepare("UPDATE brands SET brand_name = ?, brand_logo = ? WHERE id = ?");
    $updateStmt->execute([$new_brand_name, $logo_url, $brand_id]);

    // If name changed, cascade update to bikes and stock_bikes
    if ($old_brand_name !== $new_brand_name) {
        $updateBikesStmt = $conn->prepare("UPDATE bikes SET brand = ? WHERE brand = ?");
        $updateBikesStmt->execute([$new_brand_name, $old_brand_name]);

        $updateStockBikesStmt = $conn->prepare("UPDATE stock_bikes SET brand = ? WHERE brand = ?");
        $updateStockBikesStmt->execute([$new_brand_name, $old_brand_name]);
    }

    $conn->commit();

    echo json_encode([
        "status" => true,
        "message" => "Brand updated successfully",
        "data" => [
            "id" => $brand_id,
            "brand_name" => $new_brand_name,
            "brand_logo" => $logo_url
        ]
    ]);

} catch (Exception $e) {
    if (isset($conn) && $conn->inTransaction()) {
        $conn->rollBack();
    }
    http_response_code(200);
    echo json_encode([
        "status" => false,
        "message" => $e->getMessage()
    ]);
}
?>
