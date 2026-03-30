<?php
header('Content-Type: text/plain');
require_once '../config/db_connect.php';

// Hardcode a request ID to test, or pass via GET/CLI
$requestId = isset($_GET['id']) ? $_GET['id'] : (isset($argv[1]) ? $argv[1] : 1);

echo "Debugging Request ID: $requestId\n\n";

// 1. Get Request Details
$stmt = $conn->prepare("SELECT * FROM customer_requests WHERE id = :id");
$stmt->execute([':id' => $requestId]);
$request = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$request) {
    die("Request not found");
}

echo "=== Request Details ===\n";
echo "Model: '" . $request['bike_name'] . "'\n";
echo "Variant: '" . $request['bike_variant'] . "'\n";
echo "Color: '" . $request['bike_color'] . "'\n";
echo "\n";

// 2. Prepare Match Values
$model = trim($request['bike_name']);
$variant = trim($request['bike_variant']);
$color = trim($request['bike_color']);

// Handle Color Parsing logic matching get_order_summary.php
$colorSearch = $color;
if (strpos($color, '|') !== false) {
    $parts = explode('|', $color);
    $colorSearch = trim($parts[0]);
}
echo "Search Color: '" . $colorSearch . "'\n";

// 3. Run Count Query
$sql = "SELECT COUNT(*) as count FROM bikes 
        WHERE model LIKE :model 
        AND variant LIKE :variant 
        AND colors LIKE :color
        AND condition_type = 'NEW'
        AND (status IS NULL OR status = 'Available')";

echo "\n=== Query 1: Exact Logic ===\n";
echo "SQL: $sql\n";
echo "Params: model='%{$model}%', variant='%{$variant}%', color='%{$colorSearch}%'\n";

$stmt = $conn->prepare($sql);
if (!$stmt) {
    print_r($conn->errorInfo());
    die("Prepare failed");
}
$stmt->execute([
    ':model' => $model,
    ':variant' => $variant, // Note: In original code it was LIKE :variant without %
    ':color' => '%' . $colorSearch . '%'
]);
$count = $stmt->fetchColumn();
echo "Result Count: $count\n";

// 4. Dump Potential Matches
echo "\n=== Potential Matches in Bikes Table ===\n";
$dumpSql = "SELECT id, model, variant, colors, status, condition_type FROM bikes 
            WHERE model LIKE :model OR variant LIKE :variant";
$stmt = $conn->prepare($dumpSql);
$stmt->execute([
    ':model' => '%' . $model . '%',
    ':variant' => '%' . $variant . '%'
]);
$candidates = $stmt->fetchAll(PDO::FETCH_ASSOC);

// formatting output
foreach ($candidates as $bike) {
    echo "ID: " . $bike['id'] .
        " | Model: '" . $bike['model'] . "'" .
        " | Variant: '" . $bike['variant'] . "'" .
        " | Colors: '" . $bike['colors'] . "'" .
        " | Status: '" . $bike['status'] . "'" .
        " | Cond: '" . $bike['condition_type'] . "'\n";

    // Check why it failed
    $failReasons = [];
    if (stripos($bike['model'], $model) === false)
        $failReasons[] = "Model Mismatch";
    if (stripos($bike['variant'], $variant) === false)
        $failReasons[] = "Variant Mismatch";
    if (stripos($bike['colors'], $colorSearch) === false)
        $failReasons[] = "Color Mismatch";
    if ($bike['condition_type'] !== 'NEW')
        $failReasons[] = "Not NEW";
    if ($bike['status'] !== null && $bike['status'] !== 'Available')
        $failReasons[] = "Status Not Available";

    if (empty($failReasons)) {
        echo "   -> SHOULD MATCH!\n";
    } else {
        echo "   -> Failed: " . implode(", ", $failReasons) . "\n";
    }
}
?>