<?php
require_once '../config/db_connect.php';

echo "<div style='text-align:center; margin-top:50px; font-family:sans-serif;'>";

if (isset($_GET['token'])) {
    $token = $_GET['token'];

    // 1. Check if the token exists (Before updating)
    $stmt = $conn->prepare("SELECT id, email_verified FROM customers WHERE email_verification_token = ?");
    $stmt->execute([$token]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($user) {
        if ($user['email_verified'] == 1) {
            // ALREADY VERIFIED
            echo "<h1 style='color:orange;'>Already Verified</h1>";
            echo "<p>Your account has already been verified. You can log in to the MotoVista app.</p>";
            // Optional: We can clear the token here if we want to be strict, but keeping it helps shows this message.
        } else {
            // NOT VERIFIED YET - DO UPDATE
            // We keep the token (don't set to NULL) so users can click the link again and see "Already Verified"
            $update = $conn->prepare("UPDATE customers SET email_verified = 1, status = 'active', email_verified_at = NOW() WHERE id = ?");
            if ($update->execute([$user['id']])) {
                echo "<h1 style='color:green;'>Verification Successful!</h1>";
                echo "<p>Your account is now active. You can return to the MotoVista app and log in.</p>";
            } else {
                echo "<h1 style='color:red;'>Error</h1>";
                echo "<p>Something went wrong updating your status.</p>";
            }
        }
    } else {
        // Token not found (Could be invalid, or already cleared in a previous version of logic)
        // If you recently cleared tokens, they will fall here.
        echo "<h1 style='color:red;'>Invalid or Expired Link</h1>";
        echo "<p>The verification link is invalid.</p>";
    }
} else {
    echo "<h1>Access Denied</h1>";
}
echo "</div>";
?>