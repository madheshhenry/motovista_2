<?php
require_once '../config/db_connect.php';
$stmt = $conn->query("DESCRIBE customers");
while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
    echo $row['Field'] . "\n";
}
?>