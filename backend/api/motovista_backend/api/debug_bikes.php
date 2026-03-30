<?php
require_once '../config/db_connect.php';
$stmt = $conn->query("SELECT id, colors, price_details FROM bike_variants ORDER BY id DESC LIMIT 5");
$rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
echo "DEBUG OUTPUT START\n";
foreach ($rows as $row) {
    echo "ID: " . $row['id'] . "\n";
    echo "Colors Raw: " . $row['colors'] . "\n";
    echo "Colors Decoded: ";
    print_r(json_decode($row['colors'], true));
    echo "Price Raw: " . $row['price_details'] . "\n";
    echo "Price Decoded: ";
    print_r(json_decode($row['price_details'], true));
    echo "-------------------\n";
}
echo "DEBUG OUTPUT END\n";
?>