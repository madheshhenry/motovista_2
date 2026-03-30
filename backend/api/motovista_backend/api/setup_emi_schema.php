<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // 1. Create emi_ledgers table
    // Tracks the overall EMI loan for a customer and a specific bike
    $sqlLedger = "CREATE TABLE IF NOT EXISTS emi_ledgers (
        id INT AUTO_INCREMENT PRIMARY KEY,
        request_id INT, -- Link to customer request order
        title VARCHAR(255), -- Helper title e.g. Customer Name - Bike Name
        total_amount DECIMAL(10,2) NOT NULL,
        paid_amount DECIMAL(10,2) DEFAULT 0.00,
        remaining_amount DECIMAL(10,2) AS (total_amount - paid_amount) STORED,
        emi_monthly_amount DECIMAL(10,2) NOT NULL,
        interest_rate DECIMAL(5,2) DEFAULT 0.00,
        duration_months INT NOT NULL,
        start_date DATE,
        next_due_date DATE,
        status ENUM('active', 'completed', 'defaulted') DEFAULT 'active',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    )";
    $conn->exec($sqlLedger);
    echo "Table 'emi_ledgers' created or exists.\n";

    // 2. Create emi_payments table
    // Tracks individual payments made against a ledger
    $sqlPayments = "CREATE TABLE IF NOT EXISTS emi_payments (
        id INT AUTO_INCREMENT PRIMARY KEY,
        ledger_id INT NOT NULL,
        amount_paid DECIMAL(10,2) NOT NULL,
        payment_date DATE NOT NULL,
        payment_mode VARCHAR(50) DEFAULT 'Cash', -- Cash, UPI, Bank Transfer
        transaction_reference VARCHAR(255),
        remarks TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (ledger_id) REFERENCES emi_ledgers(id) ON DELETE CASCADE
    )";
    $conn->exec($sqlPayments);
    echo "Table 'emi_payments' created or exists.\n";

    echo json_encode(["status" => "success", "message" => "EMI Schema setup completed"]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Database error: " . $e->getMessage()]);
}
?>