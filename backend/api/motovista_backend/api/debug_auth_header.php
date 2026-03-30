<?php
header('Content-Type: application/json');
$headers = array_change_key_case(apache_request_headers(), CASE_LOWER);
$authHeader = $headers['authorization'] ?? '';

if (!$authHeader) {
    $authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? '';
}

file_put_contents('debug_auth_header.txt', date('[Y-m-d H:i:s] ') . "Received Auth Header: " . $authHeader . "\n", FILE_APPEND);

echo json_encode([
    "success" => true,
    "received" => $authHeader
]);
?>