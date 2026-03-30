<?php
header('Content-Type: application/json');

$data = json_decode(file_get_contents("php://input"), true);

$payload = json_encode([
    "budget"   => $data['budget'],
    "usage"    => $data['usage'],
    "priority" => $data['priority'],
    "style"    => $data['style']
]);

$ch = curl_init("http://127.0.0.1:5000/recommend");
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    "Content-Type: application/json"
]);
curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);

$response = curl_exec($ch);
curl_close($ch);

echo $response;
