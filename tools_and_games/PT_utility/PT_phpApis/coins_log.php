<?php
	function addCoinLog($userId, $from, $to, $reason, $extra, $addCoin, $firebase){
		
		date_default_timezone_set("Asia/Singapore");
		$currentDateTimeString = date("Y-m-d H:i:s");
		$log = array(
			"userId" => $userId,
			"reason" => $reason,
			"extra" => $extra,
			"dateTime" => $currentDateTimeString,
			"fromCoin" => $from,
			"toCoin" => $to,
			"addCoin" => $addCoin
		);
		
		
		
		$firebase->push("coinsLogs", $log);
		
	}

?>
