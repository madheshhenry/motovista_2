<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $tables = [];
    $stmt = $conn->query("SHOW TABLES");
    while ($row = $stmt->fetch(PDO::FETCH_NUM)) {
        $tables[] = $row[0];
    }

    $bikesTable = in_array('bikes', $tables) ? 'exists' : 'missing';
    $columns = [];
    if ($bikesTable === 'exists') {
        $stmt = $conn->query("DESCRIBE bikes");
        $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    echo json_encode([
        "bikes_table" => $bikesTable,
        "columns" => $columns
    ]);

} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
