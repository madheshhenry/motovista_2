<?php
require_once '../config/db_connect.php';

try {
    // Check if column exists
    $check = $conn->query("SHOW COLUMNS FROM admins LIKE 'workflow_stage'");
    if ($check->rowCount() == 0) {
        $conn->exec("ALTER TABLE admins ADD COLUMN workflow_stage VARCHAR(50) DEFAULT NULL");
        echo "Added workflow_stage column.\n";
    }

    $check2 = $conn->query("SHOW COLUMNS FROM admins LIKE 'active_order_id'");
    if ($check2->rowCount() == 0) {
        $conn->exec("ALTER TABLE admins ADD COLUMN active_order_id INT DEFAULT NULL");
        echo "Added active_order_id column.\n";
    }

    echo "Done.";

} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>