<?php
// Test script to verify get_brand_bikes.php output
$_SERVER['HTTP_AUTHORIZATION'] = 'Bearer mock';
$_GET['brand'] = 'Yamaha'; // Assume Yamaha exists
require_once 'get_brand_bikes.php';
?>