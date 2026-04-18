<?php
header("Content-Type: text/plain");

$hosts = [
    'smtp.gmail.com:587 (TLS)',
    'smtp.gmail.com:465 (SSL)',
    'smtp.gmail.com:2525 (ALT)',
    'google.com:80 (HTTP Test)'
];

echo "MotoVista Mail Connectivity Diagnostic\n";
echo "=====================================\n\n";

foreach ($hosts as $host_info) {
    list($host_port, $name) = explode(' ', $host_info);
    list($host, $port) = explode(':', $host_port);
    
    echo "Testing $name... ";
    
    $connection = @fsockopen($host, $port, $errno, $errstr, 5);
    
    if (is_resource($connection)) {
        echo "✅ SUCCESS! Port is OPEN.\n";
        fclose($connection);
    } else {
        echo "❌ FAILED. Error: $errstr ($errno)\n";
        echo "   (This usually means your Firewall or Antivirus is blocking Outgoing Port $port)\n";
    }
}

echo "\nPHP Configuration Check:\n";
echo "-----------------------\n";
echo "OpenSSL Extension: " . (extension_loaded('openssl') ? "✅ ENABLED" : "❌ DISABLED (Check php.ini)") . "\n";
echo "Allow URL Fopen: " . (ini_get('allow_url_fopen') ? "✅ ON" : "❌ OFF") . "\n";

?>
