<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

// Disable error display to prevent messing up JSON
ini_set('display_errors', 0);
error_reporting(E_ALL);

try {
    // Check if request is POST
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception("Method Not Allowed");
    }

    // Get JSON input
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    // Validate Input
    if (!$data || !isset($data['customer_id']) || !isset($data['bike_id']) || !isset($data['bike_color'])) {
        throw new Exception("Missing required fields");
    }

    $customer_id = $data['customer_id'];
    $customer_name = $data['customer_name'] ?? '';
    $customer_phone = $data['customer_phone'] ?? '';
    $customer_profile = $data['customer_profile'] ?? null;
    $bike_id = $data['bike_id'];
    $bike_name = $data['bike_name'] ?? '';
    $bike_variant = $data['bike_variant'] ?? '';
    $bike_color = $data['bike_color'];
    $bike_price = $data['bike_price'] ?? '';
    $selected_fittings = $data['selected_fittings'] ?? null;

    // Insert into DB
    $sql = "INSERT INTO customer_requests (customer_id, customer_name, customer_phone, customer_profile, bike_id, bike_name, bike_variant, bike_color, bike_price, selected_fittings, status)
            VALUES (:customer_id, :customer_name, :customer_phone, :customer_profile, :bike_id, :bike_name, :bike_variant, :bike_color, :bike_price, :selected_fittings, 'Pending')";

    $stmt = $conn->prepare($sql);

    $params = [
        ':customer_id' => $customer_id,
        ':customer_name' => $customer_name,
        ':customer_phone' => $customer_phone,
        ':customer_profile' => $customer_profile,
        ':bike_id' => $bike_id,
        ':bike_name' => $bike_name,
        ':bike_variant' => $bike_variant,
        ':bike_color' => $bike_color,
        ':bike_price' => $bike_price,
        ':selected_fittings' => $selected_fittings
    ];

    if ($stmt->execute($params)) {
        // Return the inserted ID to use as Order ID
        $last_id = $conn->lastInsertId();
        // Notify Admins
        try {
            require_once '../includes/FCMManager.php';
            $title = "New Bike Order! 🏍️";
            $message = "Customer " . $customer_name . " just ordered a " . $bike_name . " (" . $bike_variant . ")";
            FCMManager::notifyAdmins($title, $message, [
                "type" => "new_order",
                "screen" => "OrderSummaryActivity",
                "id" => $last_id,
                "request_id" => (int) $last_id
            ]);
        } catch (Exception $fcmError) {
            error_log("Admin FCM Error: " . $fcmError->getMessage());
        }

        echo json_encode([
            "success" => true,
            "message" => "Request sent successfully",
            "order_id" => "#ORD" . str_pad($last_id, 6, "0", STR_PAD_LEFT)
        ]);
    } else {
        throw new Exception("Database Insert Failed");
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>