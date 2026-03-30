<?php
require_once '../config/db_connect.php';

try {
    $sql = "ALTER TABLE bikes ADD COLUMN color_images TEXT NULL AFTER image_paths";
    $conn->exec($sql);
    echo "Column 'color_images' added successfully.";
} catch (PDOException $e) {
    if (strpos($e->getMessage(), "Duplicate column name") !== false) {
        echo "Column 'color_images' already exists.";
    } else {
        echo "Error: " . $e->getMessage();
    }
}
?>