<?php
/**
 * Helper class to send FCM notifications using HTTP v1 API
 * 100% Free - No recurring costs.
 */
class FCMManager
{
    private static $service_account_path = '../config/service-account.json';

    public static function notifyAdmins($title, $message, $data = [])
    {
        require_once '../config/db_connect.php';
        global $conn;

        // Persist notification for the Admin Notifications screen FIRST
        // This ensures the bell icon screen works even if push fails.
        try {
            $stmtPersist = $conn->prepare("INSERT INTO admin_notifications (title, message, type, target_screen, item_id) VALUES (?, ?, ?, ?, ?)");
            $stmtPersist->execute([
                $title,
                $message,
                $data['type'] ?? 'general',
                $data['screen'] ?? null,
                $data['id'] ?? null
            ]);
        } catch (Exception $e) {
            error_log("Failed to persist admin notification: " . $e->getMessage());
        }

        $stmt = $conn->prepare("SELECT fcm_token FROM user_fcm_tokens WHERE user_type = 'admin'");
        $stmt->execute();
        $tokens = $stmt->fetchAll(PDO::FETCH_COLUMN);

        if (empty($tokens)) {
            error_log("FCM notifyAdmins: No admin tokens found in user_fcm_tokens.");
            return ["success" => false, "message" => "No admin tokens found"];
        }

        error_log("FCM notifyAdmins: Found " . count($tokens) . " admin tokens. Sending notification...");

        $access_token = self::getAccessToken();
        if (!$access_token)
            return ["success" => false, "message" => "Failed to get Access Token"];

        $project_id = self::getProjectId();
        $results = [];

        foreach ($tokens as $token) {
            $stringData = [];
            foreach ($data as $key => $value) {
                $stringData[(string) $key] = (string) $value;
            }

            $payload = [
                "message" => [
                    "token" => $token,
                    "notification" => ["title" => $title, "body" => $message],
                    "data" => array_merge($stringData, ["title" => (string) $title, "message" => (string) $message]),
                    "android" => [
                        "priority" => "high",
                        "notification" => ["channel_id" => "high_priority_notifications"]
                    ]
                ]
            ];
            $results[] = self::callFCM($project_id, $access_token, $payload, $token, 0);
        }
        return $results;
    }

    public static function sendNotification($user_id, $title, $message, $data = [])
    {
        global $conn;
        if (!isset($conn)) {
            require_once '../config/db_connect.php';
        }

        // Persist notification for the Customer Notifications screen
        try {
            error_log("FCM: Persisting notification for user $user_id: $title");
            $stmtPersist = $conn->prepare("INSERT INTO customer_notifications (user_id, title, message, type, target_screen, item_id) VALUES (?, ?, ?, ?, ?, ?)");
            $stmtPersist->execute([
                $user_id,
                $title,
                $message,
                $data['type'] ?? 'general',
                $data['screen'] ?? null,
                $data['id'] ?? null
            ]);
            error_log("FCM: Notification persisted successfully in DB.");
        } catch (Exception $e) {
            error_log("FCM: Failed to persist customer notification: " . $e->getMessage());
        }

        // 1. Get user tokens
        $stmt = $conn->prepare("SELECT fcm_token FROM user_fcm_tokens WHERE user_id = ? AND user_type = 'customer'");
        $stmt->execute([$user_id]);
        $tokens = $stmt->fetchAll(PDO::FETCH_COLUMN);

        if (empty($tokens)) {
            error_log("FCM: No tokens found for user $user_id");
            return ["success" => false, "message" => "No tokens found for user"];
        }

        // 2. Get Access Token (Bearer)
        $access_token = self::getAccessToken();
        if (!$access_token)
            return ["success" => false, "message" => "Failed to get FCM Access Token"];

        $results = [];
        $project_id = self::getProjectId();

        foreach ($tokens as $token) {
            $stringData = [];
            foreach ($data as $key => $value) {
                $stringData[(string) $key] = (string) $value;
            }
            $stringData['title'] = (string) $title;
            $stringData['message'] = (string) $message;

            $payload = [
                "message" => [
                    "token" => $token,
                    "notification" => [
                        "title" => $title,
                        "body" => $message
                    ],
                    "data" => $stringData,
                    "android" => [
                        "priority" => "high",
                        "notification" => [
                            "channel_id" => "high_priority_notifications"
                        ]
                    ]
                ]
            ];

            $results[] = self::callFCM($project_id, $access_token, $payload, $token, $user_id);
        }

        return $results;
    }

    private static function getProjectId()
    {
        $json = self::getServiceAccountData();
        return $json['project_id'];
    }

    private static function getServiceAccountData()
    {
        if (!file_exists(self::$service_account_path)) {
            error_log("FCM Error: service-account.json not found at " . self::$service_account_path);
            throw new Exception("Service account file missing");
        }
        $data = json_decode(file_get_contents(self::$service_account_path), true);
        if (!$data || !isset($data['project_id']) || $data['project_id'] === 'YOUR_PROJECT_ID') {
            error_log("FCM Error: service-account.json is invalid or contains placeholders.");
            throw new Exception("Invalid FCM credentials");
        }
        return $data;
    }

    private static function callFCM($project_id, $access_token, $payload, $token, $user_id)
    {
        $url = "https://fcm.googleapis.com/v1/projects/$project_id/messages:send";

        $headers = [
            'Authorization: Bearer ' . $access_token,
            'Content-Type: application/json'
        ];

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $curlError = curl_error($ch);
        curl_close($ch);

        if ($response === false) {
            error_log("FCM Curl Error: " . $curlError);
            return ["success" => false, "error" => $curlError];
        }

        $res = json_decode($response, true);

        if ($httpCode !== 200) {
            error_log("FCM API Error ($httpCode): " . $response);
        }

        // Cleanup invalid tokens
        if ($httpCode == 404 || $httpCode == 410 || (isset($res['error']) && $res['error']['status'] == 'UNREGISTERED')) {
            self::removeInvalidToken($token);
        }

        return $res;
    }

    private static function removeInvalidToken($token)
    {
        require_once '../config/db_connect.php';
        global $conn;
        $stmt = $conn->prepare("DELETE FROM user_fcm_tokens WHERE fcm_token = ?");
        $stmt->execute([$token]);
    }

    /**
     * Minimal implementation to get Google Access Token using JWT
     * This avoids installing huge libraries like google-api-php-client
     */
    private static function getAccessToken()
    {
        try {
            $json = self::getServiceAccountData();

            $header = json_encode(['alg' => 'RS256', 'typ' => 'JWT']);
            $now = time();
            $payload = json_encode([
                'iss' => $json['client_email'],
                'scope' => 'https://www.googleapis.com/auth/cloud-platform',
                'aud' => 'https://oauth2.googleapis.com/token',
                'exp' => $now + 3600,
                'iat' => $now
            ]);

            $base64UrlHeader = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($header));
            $base64UrlPayload = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($payload));

            $signature_data = $base64UrlHeader . "." . $base64UrlPayload;

            if (!isset($json['private_key']) || empty($json['private_key'])) {
                error_log("FCM Error: private_key missing in service-account.json");
                return null;
            }

            $private_key = str_replace("\\n", "\n", $json['private_key']);
            if (!openssl_sign($signature_data, $signature, $private_key, 'SHA256')) {
                error_log("FCM Error: openssl_sign failed. Key might be invalid.");
                return null;
            }
            $base64UrlSignature = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($signature));

            $jwt = $signature_data . "." . $base64UrlSignature;

            // Exchange JWT for Access Token
            $ch = curl_init();
            curl_setopt($ch, CURLOPT_URL, 'https://oauth2.googleapis.com/token');
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query([
                'grant_type' => 'urn:ietf:params:oauth:grant-type:jwt-bearer',
                'assertion' => $jwt
            ]));

            $response = curl_exec($ch);
            curl_close($ch);

            $result = json_decode($response, true);

            if (!isset($result['access_token'])) {
                error_log("FCM Token Exchange Failed: " . $response);
                return null;
            }

            return $result['access_token'];
        } catch (Exception $e) {
            error_log("FCM Access Token Exception: " . $e->getMessage());
            return null;
        }
    }
}
?>