<?php
// EMAIL CONFIGURATION
// Create an App Password if using Gmail: https://myaccount.google.com/apppasswords

define('SMTP_HOST', 'smtp.gmail.com');
define('SMTP_USERNAME', 'mmadhesh225@gmail.com');
define('SMTP_PASSWORD', 'acxq nljs jnmc pqqw');
define('SMTP_PORT', 465); // 587 for TLS, 465 for SSL
define('SMTP_FROM_EMAIL', 'noreply@motovista.com');
define('SMTP_FROM_NAME', 'MotoVista Team');

// 🚀 GOOGLE SCRIPT MAIL BRIDGE (Fix for blocked SMTP ports)
define('USE_MAIL_BRIDGE', true);
define('MAIL_BRIDGE_URL', 'https://script.google.com/macros/s/AKfycbyZ5ppJILYz2wg4GOzDZ_bRSjAOX7g163DAR89VNLfCVC9CDDB5vFt1RHGTfKvjRvqzgA/exec');