<?php 
	// require('http_functions.php');
	// require('category.php');

	mysql_connect("localhost:8889", "root", "root") or die(mysql_error());
	mysql_select_db("han") or die(mysql_error());

	$query = "select * from sequence30";

	$kkman = mysql_query($query);

	// 0 = seqid, 1 =  sameid, 2 = lat, 3 = lng, 4 = G, 5 = cate, 6 = startTime, 7 = endtime 
	while($fr = mysql_fetch_row($kkman)) {
		echo $fr[1] . "," . $fr[2] . "," . $fr[3] . "," . $fr[5] . "," . $fr[6] . "," . $fr[7] . "\n";
	}
?>