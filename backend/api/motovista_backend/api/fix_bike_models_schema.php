<?php
require_once '../config/db_connect.php';

try {
    echo "Checking columns in bike_models...\n";

    // Add mandatory_fittings
    try {
        $conn->exec("ALTER TABLE bike_models ADD COLUMN mandatory_fittings JSON DEFAULT NULL");
        echo "Added mandatory_fittings column.\n";
    } catch (PDOException $e) {
        echo "mandatory_fittings (error/exists): " . $e->getMessage() . "\n";
    }

    // Add additional_fittings
    try {
        $conn->exec("ALTER TABLE bike_models ADD COLUMN additional_fittings JSON DEFAULT NULL");
        echo "Added additional_fittings column.\n";
    } catch (PDOException $e) {
        echo "additional_fittings (error/exists): " . $e->getMessage() . "\n";
    }

    // Add invoice_legal_notes
    try {
        $conn->exec("ALTER TABLE bike_models ADD COLUMN invoice_legal_notes JSON DEFAULT NULL");
        echo "Added invoice_legal_notes column.\n";
    } catch (PDOException $e) {
        echo "invoice_legal_notes (error/exists): " . $e->getMessage() . "\n";
    }

    // Add showroom_bank_details
    try {
        $conn->exec("ALTER TABLE bike_models ADD COLUMN showroom_bank_details JSON DEFAULT NULL");
        echo "Added showroom_bank_details column.\n";
    } catch (PDOException $e) {
        echo "showroom_bank_details (error/exists): " . $e->getMessage() . "\n";
    }

} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>