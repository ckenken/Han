<?php 
	// require('http_functions.php');
	// require('category.php');

	$cid = $_GET['cid'];

	mysql_connect("localhost:8889", "root", "root") or die(mysql_error());
	mysql_select_db("han") or die(mysql_error());

	if(empty($cid)) {
		echo "error";
		exit(1);
	}
	$query = "select distinct sid from fine where cid=" . $cid;

	$kkman = mysql_query($query);

	//0 = sid
	while($fr = mysql_fetch_row($kkman)) {
		$sid = $fr[0];

		$query = "select * from fine where sid = " . $sid . " and cid = " . $cid;

		$rs = mysql_query($query);

		$data = array();  // 2-dimension array

		$i = 0;
		while($fr2 = mysql_fetch_row($rs)) {
			// 0 = pid, 1 = pattern, 2 = weight, 3 = sid, 4 = cid;
			$SP = explode(",", $fr2[1]);
	
			for($j = 0; $j<count($SP); $j++) {
				$data[$j][$i] = $SP[$j];
			}
			$i++;
		}

		$flag = 0;

		for($i = 0; $i<count($data); $i++) {
			$para = "";
			for($j = 0; $j<count($data[$i]); $j++) {
				if($j == 0) 
					$para .= ("sameid = " . $data[$i][$j]);
				else 
					$para .= (" or sameid = " . $data[$i][$j]);
			}
			//echo "select avg(lat) from same where " . $para . "<br>"; 

			$query = "select avg(lat), avg(lng) from same where " . $para;

//			echo $query . "<br>" ;

			$rs = mysql_query($query);

			$fr2 = mysql_fetch_row($rs);

			$lat = floatval($fr2[0]);

			$lng = floatval($fr2[1]);

			// echo "lat = " , $lat . "<br>";
			// echo "lng = " , $lng . "<br>";

			if ($flag == 0) {
				echo $lat . "," . $lng;
				$flag = 1;
			}
			else {
				echo "," . $lat . "," . $lng;
			}
		}
		$flag = 0;
		echo "\n";
	}
	
?>
<?php
// 這裡出來的東西是  lat,lng,lat,lng,lat,lng\nlat,lng,lat,lng ....
//                   [fine-grained 1]          [fine-grained 2]




?>