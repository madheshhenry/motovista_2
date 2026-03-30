<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    $tables = [];
    $stmt = $conn->query("SHOW TABLES");
    while ($row = $stmt->fetch(PDO::FETCH_NUM)) {
        $tables[] = $row[0];
    }

    $adminsTable = in_array('admins', $tables) ? 'exists' : 'missing';
    $columns = [];
    if ($adminsTable === 'exists') {
        $stmt = $conn->query("DESCRIBE admins");
        $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    // Also check current data in admins to see if I need to seed it
    $adminData = [];
    if ($adminsTable === 'exists') {
        $stmt = $conn->query("SELECT * FROM admins LIMIT 1");
        $adminData = $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    echo json_encode([
        "tables" => $tables,
        "admins_table" => $adminsTable,
        "columns" => $columns,
        "sample_data" => $adminData
    ]);

} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
