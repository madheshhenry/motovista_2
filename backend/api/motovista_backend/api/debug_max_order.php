<?php
header('Content-Type: text/plain');
require_once '../config/db_connect.php';

try {
    echo "=== DEBUGGING ORDER ORD-26 (Customer: max) ===\n";
    
    // 1. Check customer_requests
    $stmtReq = $conn->prepare("SELECT id, customer_name, bike_name, bike_variant, bike_color, status FROM customer_requests WHERE id = 26");
    $stmtReq->execute();
    $req = $stmtReq->fetch(PDO::FETCH_ASSOC);
    if ($req) {
        echo "Request ID: " . $req['id'] . "\n";
        echo "Customer: " . $req['customer_name'] . "\n";
        echo "Bike: " . $req['bike_name'] . " (" . $req['bike_variant'] . ")\n";
        echo "Status: " . $req['status'] . "\n";
    } else {
        echo "Request 26 NOT FOUND\n";
    }

    // 2. Check registration_ledger
    $stmtLedger = $conn->prepare("SELECT * FROM registration_ledger WHERE order_id = 26");
    $stmtLedger->execute();
    $ledger = $stmtLedger->fetch(PDO::FETCH_ASSOC);
    if ($ledger) {
        echo "\nLedger Found:\n";
        echo "ID: " . $ledger['id'] . "\n";
        echo "Physical Bike ID: " . ($ledger['physical_bike_id'] ?? 'NULL') . "\n";
    } else {
        echo "\nLedger NOT FOUND for Order 26\n";
    }

    // 3. Search for a SOLD bike assigned to max
    echo "\nSearching for SOLD bike for 'max'...\n";
    $stmtBike = $conn->prepare("SELECT id, model, variant, engine_number, status, customer_name FROM bikes WHERE customer_name LIKE '%max%' OR status = 'Sold'");
    $stmtBike->execute();
    $bikes = $stmtBike->fetchAll(PDO::FETCH_ASSOC);
    foreach ($bikes as $b) {
        echo "ID: " . $b['id'] . ", Model: " . $b['model'] . ", Status: " . $b['status'] . ", Cust: " . $b['customer_name'] . ", Engine: " . $b['engine_number'] . "\n";
    }

} catch (PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>
