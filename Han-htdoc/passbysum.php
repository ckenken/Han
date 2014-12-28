<?php 
	// require('http_functions.php');
	// require('category.php');

	mysql_connect("localhost:8889", "root", "root") or die(mysql_error());
	mysql_select_db("han") or die(mysql_error());

	$query = "select g,sum(passby) from same group by g";

	$kkman = mysql_query($query);
	var_dump($kkman);
	// 0 = gid, 1 = passbysum
	while($fr = mysql_fetch_row($kkman)) {

		//var_dump($fr);

		$query = "update gcenter set passbysum =" . $fr[1] . " where Gid = " . $fr[0];
		$k = mysql_query($query);

		echo $query;
	}
	
?>