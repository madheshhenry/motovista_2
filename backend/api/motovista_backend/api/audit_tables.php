<?php
require_once '../config/db_connect.php';
header('Content-Type: text/plain');

function describe($conn, $table)
{
    echo "\n--- Structure of $table ---\n";
    try {
        $stmt = $conn->query("SHOW CREATE TABLE $table");
        $res = $stmt->fetch(PDO::FETCH_ASSOC);
        echo $res['Create Table'] . "\n";
    } catch (Exception $e) {
        echo "Error: " . $e->getMessage() . "\n";
    }
}

describe($conn, 'users');
describe($conn, 'customers');
describe($conn, 'customer_notifications');
describe($conn, 'user_fcm_tokens');
?>