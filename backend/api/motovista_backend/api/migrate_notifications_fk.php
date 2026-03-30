<?php
require_once '../config/db_connect.php';
header('Content-Type: application/json');

try {
    // 1. Drop existing foreign key constraint
    // From our audit, the constraint name is 'customer_notifications_ibfk_1'
    try {
        $conn->exec("ALTER TABLE customer_notifications DROP FOREIGN KEY customer_notifications_ibfk_1");
        echo "Dropped old foreign key constraint.\n";
    } catch (Exception $e) {
        echo "Note: Could not drop FK (might already be gone): " . $e->getMessage() . "\n";
    }

    // 2. Clear out any orphaned data that might prevent adding the new FK
    // (Optional but safer if there's test data)
    // $conn->exec("DELETE FROM customer_notifications");

    // 3. Add new foreign key referencing 'customers' table
    $conn->exec("ALTER TABLE customer_notifications 
                 ADD CONSTRAINT fk_customer_notifications_customers 
                 FOREIGN KEY (user_id) REFERENCES customers(id) 
                 ON DELETE CASCADE");

    echo "Added new foreign key referencing 'customers' table successfully.\n";

    echo json_encode(["success" => true, "message" => "Database migration completed!"]);

} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Migration failed: " . $e->getMessage()]);
}
?>