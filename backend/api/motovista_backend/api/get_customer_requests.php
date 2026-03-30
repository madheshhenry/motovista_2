<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

try {
    // Fetch all requests, joined with variant info to get colors
    // Note: We join on model_id and variant_name. 
    // This assumes bike_id in customer_requests refers to bike_models.id (which is true for V2 flow)
    $sql = "SELECT CR.*, BV.colors as variant_colors 
            FROM customer_requests CR
            LEFT JOIN bike_variants BV ON CR.bike_id = BV.model_id AND CR.bike_variant = BV.variant_name
            ORDER BY CR.created_at DESC";

    $stmt = $conn->prepare($sql);
    $stmt->execute();

    $requests = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Process each request to append Hex to bike_color
    foreach ($requests as &$req) {
        if (!empty($req['variant_colors']) && !empty($req['bike_color'])) {
            $colors = json_decode($req['variant_colors'], true);
            $targetColor = trim(strtolower($req['bike_color']));

            // Check if it already has a pipe (don't double append)
            if (strpos($targetColor, '|') === false) {
                if (json_last_error() === JSON_ERROR_NONE && is_array($colors)) {
                    foreach ($colors as $c) {
                        if (isset($c['color_name']) && trim(strtolower($c['color_name'])) === $targetColor) {
                            if (isset($c['color_hex'])) {
                                // Append Hex: "Name|#Hex"
                                $req['bike_color'] = $req['bike_color'] . "|" . $c['color_hex'];
                            }
                            break; // Found it
                        }
                    }
                }
            }
        }
        // Remove the helper column from output
        unset($req['variant_colors']);
    }

    echo json_encode([
        "success" => true,
        "data" => $requests
    ]);

} catch (Exception $e) {
    echo json_encode([
        "status" => false,
        "message" => "Error fetching requests: " . $e->getMessage()
    ]);
}
?>