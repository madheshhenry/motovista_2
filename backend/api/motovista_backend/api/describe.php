<?php
require_once '../config/db_connect.php';
header('Content-Type: text/plain');

function describe($conn, $table)
{
    echo "--- DESCRIBE $table ---\n";
    try {
        $stmt = $conn->query("DESCRIBE $table");
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            print_r($row);
        }
    } catch (Exception $e) {
        echo "Error: " . $e->getMessage() . "\n";
    }
    echo "\n";
}

describe($conn, 'customer_requests');
describe($conn, 'bike_models');
describe($conn, 'bike_variants');
describe($conn, 'bikes');
?>