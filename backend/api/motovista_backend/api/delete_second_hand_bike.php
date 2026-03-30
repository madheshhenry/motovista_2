<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type, Authorization");

require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';
// Helper function to extract Bearer token
function getBearerToken()
{
    $headers = apache_request_headers();
    if (isset($headers['Authorization'])) {
        return $headers['Authorization'];
    }
    return null;
}

// 1. Authenticate Admin
$token = (new JWT_HELPER())->validateToken(str_replace('Bearer ', '', getBearerToken()));

if (!$token) {
    http_response_code(401);
    echo json_encode(["status" => "error", "message" => "Unauthorized: Invalid or expired token"]);
    exit();
}

$userId = $token['user_id'];

// 2. Get Request Data
$data = json_decode(file_get_contents("php://input"));
if (!isset($data->bike_id)) {
    echo json_encode(["status" => "error", "message" => "Bike ID is required"]);
    exit();
}

$bike_id = $data->bike_id;

// Use global $conn from db_connect.php
if (!$conn) {
    echo json_encode(["status" => "error", "message" => "Database connection failed"]);
    exit();
}

// 3. Delete Logic
try {
    // Second hand bikes are stored in the main 'bikes' table
    $stmt = $conn->prepare("DELETE FROM bikes WHERE id = ?");
    $stmt->execute([$bike_id]);

    if ($stmt->rowCount() > 0) {
        $response = ["status" => "success", "message" => "Bike deleted successfully"];
    } else {
        $response = ["status" => "error", "message" => "Bike not found or already deleted"];
    }

} catch (Exception $e) {
    error_log("Delete SH Bike Error: " . $e->getMessage());
    $response = ["status" => "error", "message" => "Database error: " . $e->getMessage()];
}

// Close connection not strictly needed for PDO as it closes on script end, but good practice if persistent
$conn = null;
echo json_encode($response);
?>