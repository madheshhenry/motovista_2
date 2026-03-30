<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $stmt = $conn->query("SELECT id, model, colors FROM bikes LIMIT 10");
    $bikes = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Also check bike_variants if exists
    try {
        $stmtV = $conn->query("SELECT id, variant_name, colors FROM bike_variants LIMIT 10");
        $variants = $stmtV->fetchAll(PDO::FETCH_ASSOC);
    } catch (Exception $e) {
        $variants = ["error" => $e->getMessage()];
    }

    echo json_encode([
        "bikes_table" => $bikes,
        "variants_table" => $variants
    ], JSON_PRETTY_PRINT);

} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>