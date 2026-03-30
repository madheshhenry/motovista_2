<?php
header('Content-Type: text/plain');
require_once '../config/db_connect.php';

try {
    $sql = "SELECT id, brand, model_name FROM bike_models WHERE id IN (1, 2)";
    $stmt = $conn->query($sql);
    $models = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($models as $model) {
        echo "Processing Model: " . $model['model_name'] . "\n";

        $stmtVariants = $conn->prepare("SELECT variant_name, colors, price_details FROM bike_variants WHERE model_id = :mid");
        $stmtVariants->execute([':mid' => $model['id']]);
        $variants = $stmtVariants->fetchAll(PDO::FETCH_ASSOC);

        $thumbnail = null;
        $priceStart = "N/A";

        foreach ($variants as $v) {
            echo "  Variant: " . $v['variant_name'] . "\n";

            // COLORS
            $colorsRaw = $v['colors'];
            echo "  Colors Raw: " . substr($colorsRaw, 0, 50) . "...\n";
            $colors = json_decode($colorsRaw, true);
            if (json_last_error() !== JSON_ERROR_NONE) {
                echo "  JSON ERROR (Colors): " . json_last_error_msg() . "\n";
            } else {
                echo "  Colors Decoded: OK. Count: " . count($colors) . "\n";
                foreach ($colors as $i => $c) {
                    echo "    Color [$i] image_paths type: " . gettype($c['image_paths']) . "\n";
                    if (!empty($c['image_paths']) && is_array($c['image_paths'])) {
                        echo "    Found Image: " . $c['image_paths'][0] . "\n";
                        if ($thumbnail === null)
                            $thumbnail = $c['image_paths'][0];
                    }
                }
            }

            // PRICE
            $priceRaw = $v['price_details'];
            echo "  Price Raw: " . $priceRaw . "\n";
            $prices = json_decode($priceRaw, true);
            if (json_last_error() !== JSON_ERROR_NONE) {
                echo "  JSON ERROR (Price): " . json_last_error_msg() . "\n";
            } else {
                if (isset($prices['ex_showroom'])) {
                    echo "    Found Ex-Showroom: " . $prices['ex_showroom'] . "\n";
                    if ($priceStart === "N/A")
                        $priceStart = $prices['ex_showroom'];
                } else {
                    echo "    Key 'ex_showroom' missing.\n";
                }
            }
        }

        echo "  => FINAL Price: $priceStart\n";
        echo "  => FINAL Image: $thumbnail\n";
        echo "---------------------------------\n";
    }

} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>