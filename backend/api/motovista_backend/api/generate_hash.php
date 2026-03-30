<?php
// Replace 'admin123' with whatever password you want to use
$myPassword = 'admin321'; 

// This generates a secure, professional hash
$hashedPassword = password_hash($myPassword, PASSWORD_DEFAULT);

echo "<h3>Your Password:</h3> " . $myPassword;
echo "<h3>Your Generated Hash:</h3> " . $hashedPassword;
?>