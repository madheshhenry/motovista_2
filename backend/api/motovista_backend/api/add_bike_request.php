<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception("Method Not Allowed");
    }

    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    if (!$data || !isset($data['brand']) || !isset($data['full_name']) || !isset($data['mobile_number'])) {
        throw new Exception("Missing required fields");
    }

    $user_id = $data['user_id'] ?? null;
    $brand = $data['brand'];
    $model = $data['model'] ?? '';
    $features = $data['features'] ?? '';
    $full_name = $data['full_name'];
    $mobile_number = $data['mobile_number'];
    $email = $data['email'] ?? '';

    $sql = "INSERT INTO bike_requests (user_id, brand, model, features, full_name, mobile_number, email, status) 
            VALUES (:user_id, :brand, :model, :features, :full_name, :mobile_number, :email, 'Pending')";

    $stmt = $conn->prepare($sql);
    $result = $stmt->execute([
        ':user_id' => $user_id,
        ':brand' => $brand,
        ':model' => $model,
        ':features' => $features,
        ':full_name' => $full_name,
        ':mobile_number' => $mobile_number,
        ':email' => $email
    ]);

    if ($result) {
        // Notify Admins
        try {
            require_once '../includes/FCMManager.php';
            $title = "New Custom Request! ✨";
            $message = $full_name . " is interested in " . $brand . " " . $model;
            $last_id = $conn->lastInsertId();
            FCMManager::notifyAdmins($title, $message, [
                "type" => "new_bike_request",
                "screen" => "OrderSummaryActivity", // Admin sees summaries in OrderSummaryActivity
                "id" => $last_id,
                "request_id" => (int) $last_id
            ]);
        } catch (Exception $fcmError) {
            error_log("Admin FCM Error: " . $fcmError->getMessage());
        }

        echo json_encode(["success" => true, "message" => "Request submitted successfully"]);
    } else {
        throw new Exception("Database insert failed");
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>