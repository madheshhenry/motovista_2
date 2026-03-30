<?php
require_once __DIR__ . '/../config/db_connect.php';

$res = [
    'time' => date('Y-m-d H:i:s'),
    'path' => __FILE__
];

try {
    $stmtTokens = $conn->query("SELECT user_type, COUNT(*) as count FROM user_fcm_tokens GROUP BY user_type");
    $res['token_summary'] = $stmtTokens->fetchAll(PDO::FETCH_ASSOC);

    $stmtNotifs = $conn->query("SELECT * FROM admin_notifications ORDER BY created_at DESC LIMIT 5");
    $res['recent_admin_notifs'] = $stmtNotifs->fetchAll(PDO::FETCH_ASSOC);

    $stmtAllAdmins = $conn->query("SELECT user_id, device_name, user_type FROM user_fcm_tokens WHERE user_type = 'admin'");
    $res['admin_tokens'] = $stmtAllAdmins->fetchAll(PDO::FETCH_ASSOC);

    $res['success'] = true;
} catch (Exception $e) {
    $res['success'] = false;
    $res['error'] = $e->getMessage();
}

$output = json_encode($res, JSON_PRETTY_PRINT);
file_put_contents(__DIR__ . '/debug_internal_log.txt', $output);
echo $output;
?>