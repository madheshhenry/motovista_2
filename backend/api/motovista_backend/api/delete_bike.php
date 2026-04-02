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
// Optional: Check role
// if ($token['role'] !== 'admin') { ... }

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
    $conn->beginTransaction();

    // The ID passed from the frontend for NEW bikes belongs to the 'bike_models' table
    $fetchStmt = $conn->prepare("SELECT brand, model_name FROM bike_models WHERE id = ?");
    $fetchStmt->execute([$bike_id]);
    $bikeInfo = $fetchStmt->fetch(PDO::FETCH_ASSOC);

    if (!$bikeInfo) {
        $conn->rollBack();
        echo json_encode(["status" => "error", "message" => "Bike not found"]);
        exit();
    }

    $brand = $bikeInfo['brand'];
    $model = $bikeInfo['model_name'];

    // Delete associated variants
    $deleteVariants = $conn->prepare("DELETE FROM bike_variants WHERE model_id = ?");
    $deleteVariants->execute([$bike_id]);

    // Delete the master model
    $stmt3 = $conn->prepare("DELETE FROM bike_models WHERE id = ?");
    $stmt3->execute([$bike_id]);

    if ($stmt3->rowCount() > 0) {
        // Cascade delete all associated NEW stock bikes in both tables
        $deleteStockBikesLegacy = $conn->prepare("DELETE FROM bikes WHERE brand = ? AND model = ? AND condition_type = 'NEW'");
        $deleteStockBikesLegacy->execute([$brand, $model]);

        $deleteStockBikes = $conn->prepare("DELETE FROM stock_bikes WHERE brand = ? AND model = ? AND condition_type = 'NEW'");
        $deleteStockBikes->execute([$brand, $model]);

        $conn->commit();
        $response = ["status" => "success", "message" => "Bike model and associated stock deleted successfully"];
    } else {
        $conn->rollback();
        $response = ["status" => "error", "message" => "Failed to delete bike model"];
    }

} catch (Exception $e) {
    if ($conn->inTransaction()) {
        $conn->rollback();
    }
    error_log("Delete Bike Error: " . $e->getMessage());
    $response = ["status" => "error", "message" => "Database error: " . $e->getMessage()];
}

// Close connection not strictly needed for PDO as it closes on script end, but good practice if persistent
$conn = null;
echo json_encode($response);
?>