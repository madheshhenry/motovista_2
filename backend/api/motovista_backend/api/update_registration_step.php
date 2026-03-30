<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception("Method Not Allowed");
    }

    $input = json_decode(file_get_contents('php://input'), true);
    if (!isset($input['ledger_id']) || !isset($input['step_number'])) {
        throw new Exception("Missing required fields");
    }

    $ledgerId = $input['ledger_id'];
    $step = intval($input['step_number']);

    // Fetch current state
    $sql = "SELECT * FROM registration_ledger WHERE id = :id";
    $stmt = $conn->prepare($sql);
    $stmt->execute([':id' => $ledgerId]);
    $ledger = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$ledger) {
        throw new Exception("Ledger not found");
    }

    // Validation & Logic
    $updateField = "";
    $nextStepField = "";

    // Check strict sequential order
    if ($step == 1) {
        if ($ledger['step_1_status'] == 'completed')
            throw new Exception("Step 1 already completed");
        $updateField = "step_1_status";
        $nextStepField = "step_2_status";
    } elseif ($step == 2) {
        if ($ledger['step_1_status'] != 'completed')
            throw new Exception("Step 1 is not completed yet");
        if ($ledger['step_2_status'] == 'completed')
            throw new Exception("Step 2 already completed");
        $updateField = "step_2_status";
        $nextStepField = "step_3_status";
    } elseif ($step == 3) {
        if ($ledger['step_2_status'] != 'completed')
            throw new Exception("Step 2 is not completed yet");
        if ($ledger['step_3_status'] == 'completed')
            throw new Exception("Step 3 already completed");
        $updateField = "step_3_status";
        $nextStepField = "step_4_status";
    } elseif ($step == 4) {
        if ($ledger['step_3_status'] != 'completed')
            throw new Exception("Step 3 is not completed yet");
        if ($ledger['step_4_status'] == 'completed')
            throw new Exception("Step 4 already completed");
        $updateField = "step_4_status";
        $nextStepField = ""; // No next step
    } else {
        throw new Exception("Invalid step number");
    }

    // Update Query
    $updateSql = "UPDATE registration_ledger SET $updateField = 'completed'";
    if ($nextStepField) {
        $updateSql .= ", $nextStepField = 'pending'"; // Unlock next step
    }
    $updateSql .= " WHERE id = :id";

    $updateStmt = $conn->prepare($updateSql);
    if ($updateStmt->execute([':id' => $ledgerId])) {
        // TRIGGER: If step 1 (Insurance Process) is completed, add to insurance_ledger
        if ($step == 1) {
            try {
                // Calculate expiry dates: 1 year for full, 5 years for third-party
                $fullExpiry = date('Y-m-d', strtotime('+1 year'));
                $thirdPartyExpiry = date('Y-m-d', strtotime('+5 years'));

                $insSql = "INSERT INTO insurance_ledger (order_id, customer_id, customer_name, bike_name, full_insurance_expiry, third_party_expiry)
                           VALUES (:oid, :cid, :cname, :bname, :fe, :te)
                           ON DUPLICATE KEY UPDATE customer_name = :cname2"; // Avoid duplicate errors
                $insStmt = $conn->prepare($insSql);
                $insStmt->execute([
                    ':oid' => $ledger['order_id'],
                    ':cid' => $ledger['customer_id'],
                    ':cname' => $ledger['customer_name'],
                    ':bname' => $ledger['bike_name'],
                    ':fe' => $fullExpiry,
                    ':te' => $thirdPartyExpiry,
                    ':cname2' => $ledger['customer_name']
                ]);
            } catch (Exception $e_ins) {
                // Silent catch for secondary trigger
            }
        }
        echo json_encode(["success" => true, "message" => "Step $step completed successfully"]);
    } else {
        throw new Exception("Failed to update database");
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>