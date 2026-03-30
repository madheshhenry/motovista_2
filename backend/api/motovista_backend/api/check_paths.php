<?php
require_once '../config/db_connect.php';
header('Content-Type: text/plain');

echo "--- BIKES TABLE (Non-empty) ---\n";
try {
    $stmt = $conn->query("SELECT id, model, image_paths FROM bikes WHERE image_paths != '[]' AND image_paths != '' LIMIT 10");
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "ID: " . $row['id'] . " | Model: " . $row['model'] . " | Paths: " . $row['image_paths'] . "\n";
    }
} catch (Exception $e) {
    echo "Error BIKES: " . $e->getMessage() . "\n";
}

echo "\n--- BIKE_VARIANTS TABLE ---\n";
try {
    $stmt = $conn->query("SELECT id, variant_name, colors FROM bike_variants LIMIT 5");
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "ID: " . $row['id'] . " | Variant: " . $row['variant_name'] . " | Colors: " . $row['colors'] . "\n";
    }
} catch (Exception $e) {
    echo "Error BIKE_VARIANTS: " . $e->getMessage() . "\n";
}

echo "\n--- EMI_LEDGERS JOIN CUSTOMER_REQUESTS ---\n";
try {
    $stmt = $conn->query("SELECT el.id, cr.bike_name, cr.bike_color FROM emi_ledgers el JOIN customer_requests cr ON el.request_id = cr.id LIMIT 5");
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "Ledger ID: " . $row['id'] . " | Bike: " . $row['bike_name'] . " | Color: " . $row['bike_color'] . "\n";
    }
} catch (Exception $e) {
    echo "Error EMI_LEDGERS: " . $e->getMessage() . "\n";
}
?>