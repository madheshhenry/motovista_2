<?php
header("Content-Type: application/json");

require_once "../config/db_connect.php";
// require_once "../config/jwt_helper.php"; // enable later if needed

// ------------------------------
// 1. Read input from Android
// ------------------------------
$data = json_decode(file_get_contents("php://input"), true);

if (!$data) {
    echo json_encode(["success" => false, "message" => "Invalid input"]);
    exit;
}

$user_id  = $data['user_id'] ?? 0;
$budget   = $data['budget'] ?? 0;
$usage    = $data['usage'] ?? "";
$priority = $data['priority'] ?? "";
$style    = $data['style'] ?? "";

// ------------------------------
// 2. Call Python AI API
// ------------------------------
$payload = json_encode([
    "budget"   => $budget,
    "usage"    => $usage,
    "priority" => $priority,
    "style"    => $style
]);

$ch = curl_init("http://127.0.0.1:5000/recommend");
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, ["Content-Type: application/json"]);
curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);

$ai_response = curl_exec($ch);

if ($ai_response === false) {
    echo json_encode([
        "success" => false,
        "message" => "AI service not available"
    ]);
    exit;
}

curl_close($ch);

// ------------------------------
// 3. Save AI log to DB
// ------------------------------
$stmt = $conn->prepare(
    "INSERT INTO ai_chat_logs
     (user_id, budget, bike_usage, priority, style, ai_response)
     VALUES (?, ?, ?, ?, ?, ?)"
);

$stmt->bind_param(
    "iissss",
    $user_id,
    $budget,
    $usage,
    $priority,
    $style,
    $ai_response
);

$stmt->execute();

// ------------------------------
// 4. Send AI response back
// ------------------------------
echo $ai_response;
