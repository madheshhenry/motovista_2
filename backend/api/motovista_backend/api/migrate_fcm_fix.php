<?php
require_once __DIR__ . '/../config/db_connect.php';

echo "Starting migration...\n";

try {
    // 1. Drop foreign key constraint if it exists
    // We first need to check if it exists or just try to drop it and catch the error
    try {
        $conn->exec("ALTER TABLE user_fcm_tokens DROP FOREIGN KEY user_fcm_tokens_ibfk_1");
        echo "Successfully dropped foreign key user_fcm_tokens_ibfk_1\n";
    } catch (PDOException $e) {
        echo "Note: Could not drop FK user_fcm_tokens_ibfk_1 (it might not exist): " . $e->getMessage() . "\n";
    }

    // 2. Drop old unique index if it exists
    try {
        $conn->exec("ALTER TABLE user_fcm_tokens DROP INDEX user_id");
        echo "Successfully dropped old index user_id\n";
    } catch (PDOException $e) {
        echo "Note: Could not drop index user_id: " . $e->getMessage() . "\n";
    }

    // 3. Ensure user_type column is correct
    // We check if it exists first, if not we add it.
    try {
        $conn->exec("ALTER TABLE user_fcm_tokens ADD COLUMN user_type ENUM('customer', 'admin') DEFAULT 'customer'");
        echo "Successfully added user_type column\n";
    } catch (PDOException $e) {
        // If it existing, we modify it instead to be sure
        $conn->exec("ALTER TABLE user_fcm_tokens MODIFY COLUMN user_type ENUM('customer', 'admin') DEFAULT 'customer'");
        echo "Successfully verified/modified user_type column\n";
    }

    // 4. Create new composite unique index
    try {
        $conn->exec("ALTER TABLE user_fcm_tokens ADD UNIQUE KEY idx_user_type_token (user_id, user_type, fcm_token(255))");
        echo "Successfully added new composite unique index\n";
    } catch (PDOException $e) {
        echo "Note: Could not add unique index (it might already exist): " . $e->getMessage() . "\n";
    }

    echo "Migration completed successfully.\n";

} catch (Exception $e) {
    echo "CRITICAL ERROR: " . $e->getMessage() . "\n";
    exit(1);
}
?>