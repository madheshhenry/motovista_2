<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';
require_once '../config/jwt_helper.php';

try {
    // 1. Verify Admin Token
    $headers = array_change_key_case(apache_request_headers(), CASE_LOWER);
    $authHeader = $headers['authorization'] ?? '';

    if (!$authHeader) {
        $authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
    }

    if (!$authHeader)
        throw new Exception("Authorization token missing");

    $token = str_replace('Bearer ', '', $authHeader);
    $jwt = new JWT_HELPER();
    $payload = $jwt->validateToken($token);
    if (!$payload)
        throw new Exception("Unauthorized access");

    $data = json_decode(file_get_contents("php://input"));

    if (!isset($data->id) || !isset($data->source_table)) {
        throw new Exception("ID and Source Table are required");
    }

    $id = intval($data->id);
    $table = $data->source_table;

    // Security: Whitelist tables
    if (!in_array($table, ['bikes', 'stock_bikes'])) {
        throw new Exception("Invalid source table");
    }

    $sql = "DELETE FROM `$table` WHERE id = :id";
    $stmt = $conn->prepare($sql);
    $stmt->bindParam(':id', $id);

    if ($stmt->execute()) {
        if ($stmt->rowCount() > 0) {
            echo json_encode(["status" => true, "message" => "Item deleted successfully"]);
        } else {
            // Success false if no rows found
            echo json_encode(["status" => false, "message" => "Item not found in " . $table]);
        }
    } else {
        throw new Exception("Failed to delete item");
    }

} catch (Exception $e) {
    http_response_code(200);
    echo json_encode(["status" => false, "message" => $e->getMessage()]);
}
?>