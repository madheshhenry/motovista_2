<?php
header("Content-Type: application/json; charset=UTF-8");
require_once '../config/db_connect.php';

try {
    $adminId = isset($_GET['id']) ? (int)$_GET['id'] : 0;
    $action = isset($_GET['action']) ? $_GET['action'] : 'approve';

    if (!$adminId) {
        echo json_encode(["success" => false, "message" => "Missing admin ID"]);
        exit;
    }

    $status = ($action === 'approve') ? 1 : 0;
    $stmt = $conn->prepare("UPDATE admins SET is_approved = ? WHERE id = ?");
    
    if ($stmt->execute([$status, $adminId])) {
        echo json_encode(["success" => true, "message" => "Admin status updated to " . ($status ? "Approved" : "Pending")]);
    } else {
        echo json_encode(["success" => false, "message" => "Update failed"]);
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}
?>
