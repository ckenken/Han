<?php 
	// require('http_functions.php');
	// require('category.php');

	$id = $_GET['id'];

	mysql_connect("localhost:8889", "root", "root") or die(mysql_error());
	mysql_select_db("han") or die(mysql_error());

	if(empty($id)) {
		$query = "select * from same";
	}
	else {
		$query = "select * from same where sameid = " . $id;
	} 

	$kkman = mysql_query($query);

	// 0 = sameid, 1 = lat, 2 = lng, 3 = G, 4 = cate, 5 = passby, 6 = appear 
	while($fr = mysql_fetch_row($kkman)) {
		echo $fr[0] . "," . $fr[1] . "," . $fr[2] . "," . $fr[3] . "," . $fr[4] . "," . $fr[5] . "," . $fr[6]. "\n";
	}
?>