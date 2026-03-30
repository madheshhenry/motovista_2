<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

$host = "localhost";
$db_name = "android_app_db";
$username = "root";
$password = "";

try {
    $conn = new PDO("mysql:host=$host;dbname=$db_name", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    echo "Connected successfully to $db_name\n";

    echo "\n--- Tables ---\n";
    $stmt = $conn->query("SHOW TABLES");
    print_r($stmt->fetchAll(PDO::FETCH_COLUMN));

    echo "\n--- customer_notifications Table Structure ---\n";
    try {
        $stmt = $conn->query("DESCRIBE customer_notifications");
        print_r($stmt->fetchAll(PDO::FETCH_ASSOC));
    } catch (Exception $e) {
        echo "Error describing customer_notifications: " . $e->getMessage() . "\n";
    }

    echo "\n--- user_fcm_tokens Table Structure ---\n";
    try {
        $stmt = $conn->query("DESCRIBE user_fcm_tokens");
        print_r($stmt->fetchAll(PDO::FETCH_ASSOC));
    } catch (Exception $e) {
        echo "Error describing user_fcm_tokens: " . $e->getMessage() . "\n";
    }

    echo "\n--- Recent Notifications (Last 10) ---\n";
    try {
        $stmt = $conn->query("SELECT * FROM customer_notifications ORDER BY id DESC LIMIT 10");
        print_r($stmt->fetchAll(PDO::FETCH_ASSOC));
    } catch (Exception $e) {
        echo "Error fetching notifications: " . $e->getMessage() . "\n";
    }

    echo "\n--- Recent FCM Tokens ---\n";
    try {
        $stmt = $conn->query("SELECT * FROM user_fcm_tokens ORDER BY updated_at DESC LIMIT 5");
        print_r($stmt->fetchAll(PDO::FETCH_ASSOC));
    } catch (Exception $e) {
        echo "Error fetching tokens: " . $e->getMessage() . "\n";
    }

} catch (PDOException $e) {
    echo "Connection failed: " . $e->getMessage() . "\n";
}
?>