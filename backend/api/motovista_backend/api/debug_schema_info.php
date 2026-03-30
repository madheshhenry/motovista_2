<?php
require_once __DIR__ . '/../config/db_connect.php';
$out = "";
try {
    $stmt = $conn->query("SHOW CREATE TABLE user_fcm_tokens");
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    $out .= $row['Create Table'] . "\n\n";

    $stmt = $conn->query("SELECT * FROM user_fcm_tokens");
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $out .= "Current Data Count: " . count($rows) . "\n";
    $out .= print_r($rows, true) . "\n";

} catch (Exception $e) {
    $out .= "ERROR: " . $e->getMessage() . "\n";
}
file_put_contents(__DIR__ . '/schema_info_log.txt', $out);
echo "Done\n";
?>