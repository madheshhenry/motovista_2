<?php
require_once '../config/db_connect.php';

try {
    // Check if column exists
    $checkSql = "SHOW COLUMNS FROM bike_requests LIKE 'user_id'";
    $stmt = $conn->prepare($checkSql);
    $stmt->execute();

    if ($stmt->rowCount() == 0) {
        // Add user_id column
        $sql = "ALTER TABLE bike_requests ADD COLUMN user_id INT(11) DEFAULT NULL AFTER id";
        $conn->exec($sql);
        echo "Column 'user_id' added successfully.<br>";

        // Add index for performance
        $indexSql = "ALTER TABLE bike_requests ADD INDEX idx_user_id (user_id)";
        $conn->exec($indexSql);
        echo "Index on 'user_id' added successfully.<br>";
    } else {
        echo "Column 'user_id' already exists.<br>";
    }

} catch (PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>