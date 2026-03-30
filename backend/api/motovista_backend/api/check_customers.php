<?php
require_once __DIR__ . '/../config/db_connect.php';
$out = "";
try {
    $stmt = $conn->query("SHOW CREATE TABLE customers");
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    $out .= "Table customers:\n" . $row['Create Table'] . "\n\n";

} catch (Exception $e) {
    $out .= "ERROR: " . $e->getMessage() . "\n";
}
file_put_contents(__DIR__ . '/customers_structure.txt', $out);
echo "Done\n";
?>