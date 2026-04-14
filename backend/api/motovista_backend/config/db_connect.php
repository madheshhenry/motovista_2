<?php
$host = "127.0.0.1";
$db_name = "android_app_db";
$username = "root";
$password = "";

try {
    $conn = new PDO("mysql:host=$host;dbname=$db_name", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    // DO NOT ECHO HERE. It breaks JSON APIs.
    // Let the calling script handle the connection failure (chk for $conn) or throw exception.
    // We can just exit with 500 if we want, but better to let register.php try-catch handle it.
    // For now we do nothing, $conn will be null or script dies depending on error handling.
    // Ideally log it..
    error_log("DB Connection failed: " . $e->getMessage());
}