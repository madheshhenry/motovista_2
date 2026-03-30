<?php
require_once 'config/jwt_helper.php';

$jwt = new JWT_HELPER();
$token = $jwt->generateToken(1, 'test@example.com');
echo "Generated Token: $token\n\n";

$payload = $jwt->validateToken($token);
if ($payload) {
    echo "Validation Successful!\n";
    print_r($payload);
} else {
    echo "Validation Failed!\n";
    if (file_exists('api/debug_token_validation.txt')) {
        echo "Reason: " . file_get_contents('api/debug_token_validation.txt');
    } else {
        echo "Reason: Unknown (log file not found)\n";
    }
}
?>