<?php
require_once '../config/db_connect.php';

$message = "";

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $username = trim($_POST['username']);
    $email = trim($_POST['email']);
    $password = trim($_POST['password']);

    if (!empty($username) && !empty($email) && !empty($password)) {
        // Hash the password
        $hashed_password = password_hash($password, PASSWORD_DEFAULT);

        try {
            // Check if email or username exists
            $checkStmt = $conn->prepare("SELECT id FROM admins WHERE email = ? OR username = ?");
            $checkStmt->execute([$email, $username]);

            if ($checkStmt->rowCount() > 0) {
                // Update existing
                $sql = "UPDATE admins SET password = ?, username = ? WHERE email = ?";
                $stmt = $conn->prepare($sql);
                $stmt->execute([$hashed_password, $username, $email]);
                $message = "<div style='color:green;'>Admin updated successfully! You can now login.</div>";
            } else {
                // Insert new
                $sql = "INSERT INTO admins (username, email, password) VALUES (?, ?, ?)";
                $stmt = $conn->prepare($sql);
                $stmt->execute([$username, $email, $hashed_password]);
                $message = "<div style='color:green;'>New Admin created successfully! You can now login.</div>";
            }
        } catch (PDOException $e) {
            $message = "<div style='color:red;'>Error: " . $e->getMessage() . "</div>";
        }
    } else {
        $message = "<div style='color:red;'>All fields are required!</div>";
    }
}
?>

<!DOCTYPE html>
<html>

<head>
    <title>Create Admin User</title>
    <style>
        body {
            font-family: sans-serif;
            padding: 20px;
        }

        form {
            max_width: 400px;
            margin: 0 auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        input {
            width: 100%;
            padding: 10px;
            margin: 10px 0;
            box-sizing: border-box;
        }

        button {
            width: 100%;
            padding: 10px;
            background: #007bff;
            color: white;
            border: none;
            cursor: pointer;
        }

        button:hover {
            background: #0056b3;
        }
    </style>
</head>

<body>
    <h2 style="text-align:center;">Create / Update Admin</h2>
    <?= $message ?>
    <form method="POST">
        <label>Username</label>
        <input type="text" name="username" placeholder="e.g. admin" required>

        <label>Email (Official ID)</label>
        <input type="email" name="email" placeholder="e.g. your@email.com" required>

        <label>Password</label>
        <input type="text" name="password" placeholder="Enter new password" required>

        <button type="submit">Save Admin</button>
    </form>
</body>

</html>