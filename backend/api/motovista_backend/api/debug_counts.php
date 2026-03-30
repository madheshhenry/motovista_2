<?php
header('Content-Type: text/plain');
require_once '../config/db_connect.php';

try {
    echo "=== MODELS ===\n";
    $stmt = $conn->query("SELECT id, brand, model_name FROM bike_models");
    $models = $stmt->fetchAll(PDO::FETCH_ASSOC);
    foreach ($models as $m) {
        echo "ID: " . $m['id'] . ", " . $m['brand'] . " " . $m['model_name'] . "\n";

        $stmtV = $conn->prepare("SELECT COUNT(*) FROM bike_variants WHERE model_id = ?");
        $stmtV->execute([$m['id']]);
        $count = $stmtV->fetchColumn();
        echo "  - Variant Count: " . $count . "\n";
    }

} catch (PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>