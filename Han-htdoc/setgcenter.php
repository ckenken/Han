<?php 
	// require('http_functions.php');
	// require('category.php');

	$gid = $_GET['gid'];

	mysql_connect("localhost:8889", "root", "root") or die(mysql_error());
	mysql_select_db("han") or die(mysql_error());

	$query = "select cate from same where G = " . $gid;

	$kkman = mysql_query($query);

	// 0 = cate
	$fr = mysql_fetch_row($kkman);  // 只拿第一個人

	$query = "update gcenter set cate ='" . $fr[0] . "' where Gid = " . $gid;

	echo $query . "<br>";

	$kkman = mysql_query($query);
	
?>