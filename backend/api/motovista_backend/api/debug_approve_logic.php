<?php
header('Content-Type: text/plain');
require_once '../config/db_connect.php';

$requestId = isset($_GET['id']) ? $_GET['id'] : (isset($argv[1]) ? $argv[1] : 23);
echo "Debugging Approval for Request ID: $requestId\n\n";

// 1. Fetch Request
$reqSql = "SELECT customer_name, bike_name, bike_variant, bike_color, status FROM customer_requests WHERE id = :id";
$reqStmt = $conn->prepare($reqSql);
$reqStmt->execute([':id' => $requestId]);
$reqData = $reqStmt->fetch(PDO::FETCH_ASSOC);

if (!$reqData) {
    die("Request ID $requestId not found.\n");
}

echo "Request Data:\n";
print_r($reqData);

$model = trim($reqData['bike_name']);
$variant = trim($reqData['bike_variant']);
$color = trim($reqData['bike_color']);

// Color Parse
$colorSearch = $color;
if (strpos($color, '|') !== false) {
    $parts = explode('|', $color);
    $colorSearch = trim($parts[0]);
}

echo "\nSearch Params:\n";
echo "Model: '$model'\n";
echo "Variant: '$variant'\n";
echo "Color: '$colorSearch'\n";

// 2. dry run FIND available bike
$findSql = "SELECT id, model, variant, colors, status FROM bikes 
            WHERE model LIKE :model 
            AND variant LIKE :variant 
            AND colors LIKE :color 
            AND condition_type = 'NEW'
            AND (status IS NULL OR status = 'Available')
            ORDER BY date ASC 
            LIMIT 1";

$stmt = $conn->prepare($findSql);
$stmt->execute([
    ':model' => $model,
    ':variant' => $variant,
    ':color' => '%' . $colorSearch . '%'
]);
$bike = $stmt->fetch(PDO::FETCH_ASSOC);

if ($bike) {
    echo "\nMATCH FOUND: Bike ID " . $bike['id'] . "\n";
    print_r($bike);

    // 3. Try Update
    echo "\nAttempting Update...\n";
    $updateBikeSql = "UPDATE bikes 
                      SET status = 'Sold', 
                          sold_date = NOW(), 
                          customer_name = :custName,
                          left_inventory_date = NOW()
                      WHERE id = :bikeId";

    $upStmt = $conn->prepare($updateBikeSql);
    $res = $upStmt->execute([
        ':custName' => $reqData['customer_name'],
        ':bikeId' => $bike['id']
    ]);

    if ($res) {
        echo "Update Query Success. Rows affected: " . $upStmt->rowCount() . "\n";
    } else {
        echo "Update Query Failed.\n";
        print_r($upStmt->errorInfo());
    }

} else {
    echo "\nNO MATCHING BIKE FOUND IN INVENTORY!\n";
    echo "This explains why it wasn't marked as sold.\n";

    // Dump Partial Matches again
    echo "\nChecking partial matches:\n";
    $dump = $conn->query("SELECT id, model, variant, colors, status FROM bikes WHERE model LIKE '%$model%'")->fetchAll(PDO::FETCH_ASSOC);
    print_r($dump);
}
?>