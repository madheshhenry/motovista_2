<?php
require_once __DIR__ . '/../config/db_connect.php';
$out = "";
try {
    $stmt = $conn->query("SELECT * FROM users");
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $out .= "Users Table Count: " . count($rows) . "\n";
    $out .= print_r($rows, true) . "\n";

} catch (Exception $e) {
    $out .= "ERROR: " . $e->getMessage() . "\n";
}
file_put_contents(__DIR__ . '/users_debug.txt', $out);
echo "Done\n";
?>