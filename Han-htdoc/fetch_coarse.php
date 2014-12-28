<?php 
	// require('http_functions.php');
	// require('category.php');

	$length = $_GET['length'];

	mysql_connect("localhost:8889", "root", "root") or die(mysql_error());
	mysql_select_db("han") or die(mysql_error());

	$query = "select * from coarse where length = " . $length;

	$kkman = mysql_query($query);

	// 0 = cid, 1 = coarsepattern, 2 = weightsum, 3 = length
	while($fr = mysql_fetch_row($kkman)) {
		echo $fr[0] . " " . $fr[1] . " " . $fr[2] . " " . $fr[3] . "\n";
	}
?>