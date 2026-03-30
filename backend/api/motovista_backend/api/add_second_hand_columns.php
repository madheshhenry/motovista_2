<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $columnsToAdd = [
        'odometer' => "VARCHAR(50) DEFAULT NULL",
        'owner_details' => "VARCHAR(100) DEFAULT NULL",
        'condition_details' => "TEXT DEFAULT NULL"
    ];

    $messages = [];

    foreach ($columnsToAdd as $col => $def) {
        $checkSql = "SHOW COLUMNS FROM bikes LIKE '$col'";
        $stmt = $conn->prepare($checkSql);
        $stmt->execute();

        if ($stmt->rowCount() == 0) {
            $sql = "ALTER TABLE bikes ADD COLUMN $col $def";
            $conn->exec($sql);
            $messages[] = "Column '$col' added.";
        } else {
            $messages[] = "Column '$col' already exists.";
        }
    }

    echo json_encode(["status" => "success", "messages" => $messages]);

} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Database error: " . $e->getMessage()]);
}
?>