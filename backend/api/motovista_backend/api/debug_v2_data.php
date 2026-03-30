<?php
require_once '../config/db_connect.php';

try {
    // Get latest model
    $stmt = $conn->query("SELECT * FROM bike_models ORDER BY id DESC LIMIT 1");
    $model = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$model) {
        die("No models found.\n");
    }

    echo "LATEST MODEL (ID: " . $model['id'] . "):\n";
    print_r($model);

    // Get variants for this model
    $stmtVar = $conn->prepare("SELECT * FROM bike_variants WHERE model_id = :mid");
    $stmtVar->execute([':mid' => $model['id']]);
    $variants = $stmtVar->fetchAll(PDO::FETCH_ASSOC);

    echo "\nVARIANTS (" . count($variants) . "):\n";
    foreach ($variants as $v) {
        echo "  [ID: " . $v['id'] . "] " . $v['variant_name'] . "\n";
        echo "    Price JSON: " . $v['price_details'] . "\n";
        echo "    Colors JSON: " . $v['colors'] . "\n";
    }

} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>