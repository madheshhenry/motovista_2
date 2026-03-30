<?php
header('Content-Type: text/plain');
require_once '../config/db_connect.php';

echo "=== STARTING WORKFLOW SIMULATION ===\n\n";

// 1. Setup: Creating a unique test Model to isolate this test
$testModel = "TEST-R15-V" . rand(100, 999);
$testVariant = "Pro";
$testColor = "Matte Black";

echo "1. Creating Test Stock for: $testModel | $testVariant | $testColor\n";
echo "   Adding 3 Units...\n";

$insertSql = "INSERT INTO bikes (brand, model, variant, colors, engine_number, chassis_number, date, condition_type, status) VALUES 
(:brand, :model, :variant, :color, :eng1, :chs1, NOW(), 'NEW', 'Available'),
(:brand, :model, :variant, :color, :eng2, :chs2, NOW(), 'NEW', 'Available'),
(:brand, :model, :variant, :color, :eng3, :chs3, NOW(), 'NEW', 'Available')";

$stmt = $conn->prepare($insertSql);
$stmt->execute([
    ':brand' => 'TestBrand',
    ':model' => $testModel,
    ':variant' => $testVariant,
    ':color' => $testColor,
    ':eng1' => 'ENG' . rand(1000, 9999),
    ':chs1' => 'CHS' . rand(1000, 9999),
    ':eng2' => 'ENG' . rand(1000, 9999),
    ':chs2' => 'CHS' . rand(1000, 9999),
    ':eng3' => 'ENG' . rand(1000, 9999),
    ':chs3' => 'CHS' . rand(1000, 9999)
]);

// 2. Check Availability (Should be 3)
echo "\n2. Verify Initial Availability (Expect 3)...\n";
$stockSql = "SELECT COUNT(*) FROM bikes WHERE model = '$testModel' AND status = 'Available'";
$count = $conn->query($stockSql)->fetchColumn();
echo "   Count is: $count " . ($count == 3 ? "[PASS]" : "[FAIL]") . "\n";

// 3. Create a Customer Request
echo "\n3. Simulate Customer Request...\n";
$conn->exec("INSERT INTO customer_requests (customer_name, bike_name, bike_variant, bike_color, status) 
             VALUES ('Test User', '$testModel', '$testVariant', '$testColor', 'pending')");
$requestId = $conn->lastInsertId();
echo "   Created Request ID: $requestId\n";

// 4. Simulate Admin Approval (Trigger inventory update)
echo "\n4. Approving Request (Executing logic)...\n";

// -- Logic Copy from update_request_status.php --
$updateBikeSql = "UPDATE bikes 
   SET status = 'Sold', 
       sold_date = NOW(), 
       customer_name = 'Test User',
       left_inventory_date = NOW()
   WHERE id = (
       SELECT id FROM (
           SELECT id FROM bikes 
           WHERE model = '$testModel' 
           AND status = 'Available'
           ORDER BY date ASC 
           LIMIT 1
       ) as tmp
   )";
$conn->exec($updateBikeSql);
// -----------------------------------------------
echo "   Approval Logic Executed.\n";

// 5. Verify Results
echo "\n5. Verifying Final State...\n";

// Check Available (Should be 2)
$availCount = $conn->query("SELECT COUNT(*) FROM bikes WHERE model = '$testModel' AND status = 'Available'")->fetchColumn();
echo "   Available Stock: $availCount " . ($availCount == 2 ? "[PASS]" : "[FAIL] (Expected 2)") . "\n";

// Check Sold (Should be 1)
$soldCount = $conn->query("SELECT COUNT(*) FROM bikes WHERE model = '$testModel' AND status = 'Sold'")->fetchColumn();
echo "   Sold Units: $soldCount " . ($soldCount == 1 ? "[PASS]" : "[FAIL] (Expected 1)") . "\n";

// Check Details
$soldBike = $conn->query("SELECT customer_name, sold_date FROM bikes WHERE model = '$testModel' AND status = 'Sold' LIMIT 1")->fetch(PDO::FETCH_ASSOC);
echo "   Sold Bike Customer: " . $soldBike['customer_name'] . " " . ($soldBike['customer_name'] === 'Test User' ? "[PASS]" : "[FAIL]") . "\n";
echo "   Sold Bike Date: " . ($soldBike['sold_date'] ? "Set" : "NULL [FAIL]") . "\n";

echo "\n=== SIMULATION COMPLETE ===\n";
?>