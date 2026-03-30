<?php
require_once '../config/db_connect.php';
ini_set('display_errors', 1);
error_reporting(E_ALL);

try {
    // Get the most recently added bike
    $sql = "SELECT * FROM bikes ORDER BY created_at DESC LIMIT 1";
    $stmt = $conn->query($sql);
    $bike = $stmt->fetch(PDO::FETCH_ASSOC);

    echo "<pre>";
    print_r($bike);
    echo "</pre>";

} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>