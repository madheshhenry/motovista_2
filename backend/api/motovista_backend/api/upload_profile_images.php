<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';

// Folder where images will be saved
$target_dir = "../uploads/profile_pics/";
if (!file_exists($target_dir)) {
    mkdir($target_dir, 0777, true);
}

// Token validation
$headers = apache_request_headers();
$token = str_replace('Bearer ', '', $headers['Authorization'] ?? '');

if (!$token) {
    echo json_encode(["success" => false, "message" => "Unauthorized"]);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_FILES['profile_image'])) {
    $file_extension = strtolower(pathinfo($_FILES["profile_image"]["name"], PATHINFO_EXTENSION));
    $new_file_name = uniqid() . '.' . $file_extension;
    $target_file = $target_dir . $new_file_name;

    // Check if it's a real image
    $check = getimagesize($_FILES["profile_image"]["tmp_name"]);
    if($check !== false) {
        if (move_uploaded_file($_FILES["profile_image"]["tmp_name"], $target_file)) {
            // Update the filename in the database
            $stmt = $conn->prepare("UPDATE users SET profile_image = ? WHERE token = ?");
            if ($stmt->execute([$new_file_name, $token])) {
                echo json_encode(["success" => true, "message" => "Image uploaded", "image_url" => $new_file_name]);
            } else {
                echo json_encode(["success" => false, "message" => "DB Update Failed"]);
            }
        } else {
            echo json_encode(["success" => false, "message" => "Upload error"]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "Not an image"]);
    }
}
?>