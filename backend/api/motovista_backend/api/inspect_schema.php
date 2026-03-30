<?php
require_once '../config/db_connect.php';
function describe($conn, $table)
{
    if (!$conn->query("SHOW TABLES LIKE '$table'")->fetch())
        return;
    echo "Table: $table\n";
    $stmt = $conn->query("DESCRIBE $table");
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo $row['Field'] . " " . $row['Type'] . "\n";
    }
    echo "\n";
}
describe($conn, 'customer_requests');
describe($conn, 'registration_ledger');
describe($conn, 'emi_ledgers');
describe($conn, 'emi_payments');
?>