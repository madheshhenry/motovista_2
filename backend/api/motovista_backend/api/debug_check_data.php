<?php
require_once __DIR__ . '/../config/db_connect.php';

$request_id = 12; // Based on previous output

echo "Checking Request ID: $request_id\n";

// Check Customer Request
$stmt = $conn->prepare("SELECT * FROM customer_requests WHERE id = ?");
$stmt->execute([$request_id]);
$request = $stmt->fetch(PDO::FETCH_ASSOC);

if ($request) {
    echo "Customer Request Found:\n";
    print_r($request);

    $bike_id = $request['bike_id'];
    echo "Bike ID from Request: $bike_id\n";

    // Check Bike
    if ($bike_id) {
        $stmtBike = $conn->prepare("SELECT * FROM bikes WHERE id = ?");
        $stmtBike->execute([$bike_id]);
        $bike = $stmtBike->fetch(PDO::FETCH_ASSOC);

        if ($bike) {
            echo "Bike Found:\n";
            print_r($bike);
        } else {
            echo "Bike NOT Found for ID: $bike_id\n";
        }
    } else {
        echo "No Bike ID in request.\n";
    }

} else {
    echo "Customer Request NOT Found for ID: $request_id\n";
}
?>