<?php
try {
    $host = 'localhost';
    $db = 'android_app_db';
    $user = 'root';
    $pass = '';
    $charset = 'utf8mb4';

    $dsn = "mysql:host=$host;dbname=$db;charset=$charset";
    $options = [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES => false,
    ];

    $conn = new PDO($dsn, $user, $pass, $options);
    echo "SUCCESS: Connected to $db\n";

    $stmt = $conn->query("SELECT COUNT(*) as count FROM user_fcm_tokens");
    $row = $stmt->fetch();
    echo "Tokens count: " . $row['count'] . "\n";

} catch (Exception $e) {
    echo "FAILURE: " . $e->getMessage() . "\n";
    exit(1);
}
?>