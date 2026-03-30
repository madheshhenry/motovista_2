<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // Check if column 'custom_fittings' exists
    $checkSql = "SHOW COLUMNS FROM bikes LIKE 'custom_fittings'";
    $stmt = $conn->prepare($checkSql);
    $stmt->execute();

    if ($stmt->rowCount() == 0) {
        // Add custom_fittings column
        $sql = "ALTER TABLE bikes ADD COLUMN custom_fittings TEXT DEFAULT NULL";
        $conn->exec($sql);
        echo json_encode(["status" => "success", "message" => "Column 'custom_fittings' added successfully."]);
    } else {
        echo json_encode(["status" => "success", "message" => "Column 'custom_fittings' already exists."]);
    }

} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Database error: " . $e->getMessage()]);
}
?>