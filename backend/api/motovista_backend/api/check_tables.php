<?php
require_once __DIR__ . '/../config/db_connect.php';
$out = "";
try {
    $stmt = $conn->query("SHOW TABLES");
    $tables = $stmt->fetchAll(PDO::FETCH_COLUMN);
    $out .= "Tables: " . implode(", ", $tables) . "\n\n";

    if (in_array('users', $tables)) {
        $stmt = $conn->query("SHOW CREATE TABLE users");
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $out .= "Table users:\n" . $row['Create Table'] . "\n\n";
    }

    if (in_array('admins', $tables)) {
        $stmt = $conn->query("SHOW CREATE TABLE admins");
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $out .= "Table admins:\n" . $row['Create Table'] . "\n\n";
    }

} catch (Exception $e) {
    $out .= "ERROR: " . $e->getMessage() . "\n";
}
file_put_contents(__DIR__ . '/table_structures.txt', $out);
echo "Done\n";
?>