<?php 
	// require('http_functions.php');
	// require('category.php');

	$gid = $_GET['gid'];

	mysql_connect("localhost:8889", "root", "root") or die(mysql_error());
	mysql_select_db("han") or die(mysql_error());

	if(empty($gid)) {
		$query = "select * from gcenter";
	}
	else {
		$query = "select * from gcenter where gid = " . $gid;
	}
	$kkman = mysql_query($query);

	// 0 = gid, 1 = lat, 2 = lng, 3 = cate, 4 = passbySum
	while($fr = mysql_fetch_row($kkman)) {
		if(empty($gid))
			echo $fr[0] . "#" . $fr[1] . "#" . $fr[2] . "#" . $fr[3] . "#" . $fr[4] . "\n";
		else 
			echo $fr[0] . "#" . $fr[1] . "#" . $fr[2] . "#" . $fr[3] . "#" . $fr[4];
	}
?>