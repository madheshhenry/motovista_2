<?php
class JWT_HELPER
{
    private $secret_key = "Motovista_Secret_Key_2024@Deep";

    public function generateToken($user_id, $email, $role = 'customer')
    {
        $header = json_encode(['typ' => 'JWT', 'alg' => 'HS256']);
        $payload = json_encode([
            'user_id' => $user_id,
            'email' => $email,
            'role' => $role,
            'iat' => time(),
            'exp' => time() + (60 * 60 * 24 * 7) // 7 days
        ]);

        $base64UrlHeader = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($header));
        $base64UrlPayload = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($payload));

        $signature = hash_hmac('sha256', $base64UrlHeader . "." . $base64UrlPayload, $this->secret_key, true);
        $base64UrlSignature = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($signature));

        return $base64UrlHeader . "." . $base64UrlPayload . "." . $base64UrlSignature;
    }

    public function validateToken($token)
    {
        $parts = explode('.', $token);
        if (count($parts) != 3) {
            file_put_contents(__DIR__ . '/../api/debug_token_validation.txt', date('[Y-m-d H:i:s] ') . "Invalid token parts count: " . count($parts) . " for token: " . substr($token, 0, 10) . "...\n", FILE_APPEND);
            return false;
        }

        list($base64UrlHeader, $base64UrlPayload, $base64UrlSignature) = $parts;

        $signature = hash_hmac('sha256', $base64UrlHeader . "." . $base64UrlPayload, $this->secret_key, true);
        $base64UrlSignatureCheck = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($signature));

        if (!hash_equals($base64UrlSignature, $base64UrlSignatureCheck)) {
            file_put_contents(__DIR__ . '/../api/debug_token_validation.txt', date('[Y-m-d H:i:s] ') . "Signature mismatch! Provided: $base64UrlSignature, Calculated: $base64UrlSignatureCheck\n", FILE_APPEND);
            return false;
        }

        $payload = json_decode(base64_decode(str_replace(['-', '_'], ['+', '/'], $base64UrlPayload)), true);

        if (isset($payload['exp']) && $payload['exp'] < time()) {
            file_put_contents(__DIR__ . '/../api/debug_token_validation.txt', date('[Y-m-d H:i:s] ') . "Token expired! Exp: " . $payload['exp'] . ", Current: " . time() . "\n", FILE_APPEND);
            return false; // Expired
        }

        return $payload;
    }
}