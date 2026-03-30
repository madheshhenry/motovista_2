<?php
ini_set('display_errors', 1);
require_once '../config/db_connect.php';

echo "<h2>Fixing Database Schema...</h2>";

$columns = [
    "is_profile_completed" => "TINYINT(1) DEFAULT 0",
    "dob" => "VARCHAR(50) DEFAULT NULL",
    "house_no" => "VARCHAR(100) DEFAULT NULL",
    "street" => "VARCHAR(255) DEFAULT NULL",
    "city" => "VARCHAR(100) DEFAULT NULL",
    "state" => "VARCHAR(100) DEFAULT NULL",
    "pincode" => "VARCHAR(20) DEFAULT NULL",
    "pan_no" => "VARCHAR(20) DEFAULT NULL",
    "profile_image" => "VARCHAR(255) DEFAULT NULL",
    "aadhar_front" => "VARCHAR(255) DEFAULT NULL",
    "aadhar_back" => "VARCHAR(255) DEFAULT NULL"
];

foreach ($columns as $name => $definition) {
    try {
        // Try to add the column
        $sql = "ALTER TABLE customers ADD COLUMN $name $definition";
        $conn->exec($sql);
        echo "<span style='color:green;'>[SUCCESS] Added column: <b>$name</b></span><br>";
    } catch (PDOException $e) {
        // Error 1060 = Duplicate column name
        if ($e->errorInfo[1] == 1060) {
            echo "<span style='color:orange;'>[INFO] Column <b>$name</b> already exists.</span><br>";
        } else {
            echo "<span style='color:red;'>[ERROR] Failed to add <b>$name</b>: " . $e->getMessage() . "</span><br>";
        }
    }
}

echo "<h3>Done! You can now resume Profile Setup.</h3>";
?>