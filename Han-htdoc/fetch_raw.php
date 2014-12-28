<?php 
	// require('http_functions.php');
	// require('category.php');

	$para = $_GET['noise'];

	mysql_connect("localhost:8889", "root", "root") or die(mysql_error());
	mysql_select_db("han") or die(mysql_error());


	if(strcmp($para, "yes") == 0) {
		$query = "select * from raw2";
	}
	else {
		$query = "select * from raw2 where same != -1";
	}

	$kkman = mysql_query($query);

	// 0 = id, 1 = lat, 2 = lng, 3 = same, 4 = G, 5 = date, 6 = date(time) 
	while($fr = mysql_fetch_row($kkman)) {
		echo $fr[0] . "," . $fr[1] . "," . $fr[2] . "," . $fr[3] . "," . $fr[4] . "," . $fr[5] . "," . $fr[6]. "\n";
	}
?>