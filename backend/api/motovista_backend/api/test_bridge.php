<?php
header("Content-Type: text/plain");
require_once '../config/email_config.php';

echo "MotoVista Mail Bridge Diagnostic\n";
echo "===============================\n\n";

if (!defined('MAIL_BRIDGE_URL') || empty(MAIL_BRIDGE_URL)) {
    die("❌ Error: MAIL_BRIDGE_URL is not defined in email_config.php");
}

echo "Bridge URL: " . MAIL_BRIDGE_URL . "\n\n";

$test_data = [
    'to' => 'mmadhesh225@gmail.com', // Using your email for test
    'subject' => 'Bridge Test - MotoVista',
    'body' => '<h1>It Works!</h1><p>If you see this, the bridge is working perfectly.</p>'
];

echo "Sending test request... ";

$ch = curl_init(MAIL_BRIDGE_URL);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($test_data));
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
curl_setopt($ch, CURLOPT_TIMEOUT, 30);

$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curl_error = curl_error($ch);
curl_close($ch);

if ($response === false) {
    echo "❌ FAILED (CURL Error): " . $curl_error . "\n";
} else {
    echo "HTTP Code: $http_code\n";
    echo "Raw Response: " . $response . "\n\n";
    
    $data = json_decode($response, true);
    if ($data && isset($data['success']) && $data['success']) {
        echo "✅ SUCCESS! Your Google Script is working correctly.\n";
    } else {
        echo "❌ FAILED. Google Script returned an error.\n";
        echo "   (Make sure you deployed as 'Anyone' and shared access)\n";
    }
}
?>
