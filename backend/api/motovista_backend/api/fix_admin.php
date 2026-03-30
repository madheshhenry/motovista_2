<?php
require_once '../config/db_connect.php';

// The plain text password you want to use
$plain_password = "admin123";

// Generate the professional PHP hash
$new_hash = password_hash($plain_password, PASSWORD_DEFAULT);

try {
    // Update the admin table with the correct hash
    $stmt = $conn->prepare("UPDATE admins SET password = ? WHERE email = 'admin@motovista.com'");
    $stmt->execute([$new_hash]);

    if($stmt->rowCount() > 0) {
        echo "<h1>Success!</h1><p>Admin password has been reset to: <b>admin123</b></p>";
    } else {
        echo "<h1>Notice</h1><p>Admin account not found. Check if 'admin@motovista.com' exists in the table.</p>";
    }
} catch (PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>