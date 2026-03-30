<?php
header('Content-Type: text/plain');
require_once '../config/db_connect.php';

try {
    $stmt = $conn->query("SELECT * FROM bike_variants LIMIT 5");
    $variants = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($variants as $v) {
        echo "Variant ID: " . $v['id'] . "\n";
        echo "Model ID: " . $v['model_id'] . "\n";
        echo "Variant Name: " . $v['variant_name'] . "\n";
        echo "Colors (Raw JSON): " . $v['colors'] . "\n";
        echo "Price Details (Raw JSON): " . $v['price_details'] . "\n";
        echo "--------------------------------------------------\n";
    }

} catch (PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>