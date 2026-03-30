<?php
header('Content-Type: text/plain');
$log = 'c:\xampp\apache\logs\error.log';
if (file_exists($log)) {
    $lines = file($log);
    $last_lines = array_slice($lines, -100);
    echo implode("", $last_lines);
} else {
    echo "Log file not found.";
}
?>