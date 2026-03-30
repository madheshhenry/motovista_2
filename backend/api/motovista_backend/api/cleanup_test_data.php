<?php
require_once '../config/db_connect.php';

echo "Cleaning up test data...\n";

// Delete test bikes
$sqlBikes = "DELETE FROM bikes WHERE model LIKE 'TEST-R15-V%'";
$stmt = $conn->prepare($sqlBikes);
$stmt->execute();
echo "Deleted " . $stmt->rowCount() . " test bikes.\n";

// Delete test requests
$sqlReq = "DELETE FROM customer_requests WHERE bike_name LIKE 'TEST-R15-V%'";
$stmt2 = $conn->prepare($sqlReq);
$stmt2->execute();
echo "Deleted " . $stmt2->rowCount() . " test requests.\n";

echo "Cleanup complete.";
?>