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

		for($i = 0; $i<count($data); $i++) {
			$time = array();
			for($j = 0; $j<count($data[$i]); $j++) {
				$query = "select timedistribution from same where sameid = " . $data[$i][$j];

				$rs = mysql_query($query);

				// 0 = time distribution 
				$fr2 = mysql_fetch_row($rs);

				$SP2 = explode("\n", $fr2[0]);

				for($k = 0; $k<count($SP2); $k++) {
					$SP3 = explode(":", $SP2[$k]);
					$time[$k] += floatval($SP3[1]);
				}
			}
			$normal = count($data[$i]);
			for($k = 0; $k<24; $k++) {
				$time[$k] /= floatval($normal);
			}
			// 印出這個點的平均 time distribution
			for($i = 0; $i<24; $i++) {
				if($i == 0)	{
					echo $i . ": " . $time[$i];
				}
				else {
					echo "<br>" . $i . ": " . $time[$i];
				}
			}
			echo ",";
		}
		echo "\n";


// 		$flag = 0;

// 		for($i = 0; $i<count($data); $i++) {
// 			$para = "";
// 			for($j = 0; $j<count($data[$i]); $j++) {
// 				if($j == 0) 
// 					$para .= ("sameid = " . $data[$i][$j]);
// 				else 
// 					$para .= (" or sameid = " . $data[$i][$j]);
// 			}

// 			$query = "select avg(lat), avg(lng) from same where " . $para;

// 			$rs = mysql_query($query);

// 			$fr2 = mysql_fetch_row($rs);

// 			$lat = floatval($fr2[0]);

// 			$lng = floatval($fr2[1]);

// 			if ($flag == 0) {
// 				echo $lat . "," . $lng;
// 				$flag = 1;
// 			}
// 			else {
// 				echo "," . $lat . "," . $lng;
// 			}
// 		}
// 		$flag = 0;
// 		echo "\n";
	}
	
?>
<?php
// 這裡出來的東西是  lat,lng,lat,lng,lat,lng\nlat,lng,lat,lng ....
//                   [fine-grained 1]          [fine-grained 2]




?>