<?php
header("Content-Type: application/json; charset=UTF-8");

require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

try {
    // 1. Verify Admin Token
    $headers = apache_request_headers();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';

    if (!$authHeader) {
        throw new Exception("Authorization token missing");
    }

    $token = str_replace('Bearer ', '', $authHeader);
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);

    if (!$payload) {
        throw new Exception("Unauthorized access or invalid token");
    }

    // Optional: Check if the requester is an admin
    // if ($payload['role'] !== 'admin') { throw new Exception("Access denied"); }

    // 2. Validate Customer ID
    if (!isset($_GET['customer_id'])) {
        throw new Exception("Customer ID is required");
    }

    $customerId = intval($_GET['customer_id']);

    // 3. Fetch Customer Details
    $sql = "SELECT id, full_name, email, phone, created_at, 
            dob, pan_no as pan, house_no, street, city, state, pincode, 
            profile_image, aadhar_front, aadhar_back 
            FROM customers WHERE id = ?";

    $stmt = $conn->prepare($sql);
    $stmt->execute([$customerId]);

    $customer = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$customer) {
        throw new Exception("Customer not found");
    }

    // 4. Construct Composite Address
    $addressParts = [];
    if (!empty($customer['house_no']))
        $addressParts[] = $customer['house_no'];
    if (!empty($customer['street']))
        $addressParts[] = $customer['street'];
    if (!empty($customer['city']))
        $addressParts[] = $customer['city'];
    if (!empty($customer['state']))
        $addressParts[] = $customer['state'];
    if (!empty($customer['pincode']))
        $addressParts[] = $customer['pincode'];

    $customer['address'] = !empty($addressParts) ? implode(", ", $addressParts) : "N/A";

    // null checks for images
    if (!$customer['profile_image'])
        $customer['profile_image'] = "N/A";
    if (!$customer['aadhar_front'])
        $customer['aadhar_front'] = "N/A";
    if (!$customer['aadhar_back'])
        $customer['aadhar_back'] = "N/A";
    if (!$customer['dob'])
        $customer['dob'] = "N/A";
    if (!$customer['pan'])
        $customer['pan'] = "N/A";


    echo json_encode([
        "status" => true,
        "message" => "Customer details fetched successfully",
        "data" => $customer
    ]);

} catch (Exception $e) {
    http_response_code(200); // Return 200 with error message for app to handle gracefully
    echo json_encode([
        "status" => false,
        "message" => $e->getMessage(),
        "data" => null
    ]);
}
?>