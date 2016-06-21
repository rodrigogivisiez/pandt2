<?php
	function addCoinToUser($userId, $count, $reason, $extra, $firebase){
		$currentUserCoinsString = $firebase->get("coins/".$userId);
			
		if(empty($currentUserCoinsString)){
			$currentUserCoins = 0;
		}
		else{
			$currentUserCoins = (int)json_decode($currentUserCoinsString);
		}
		
		$currentUserCoins += $count;
		$firebase->set("coins/".$userId, $currentUserCoins);
		
		date_default_timezone_set("Asia/Singapore");
		$currentDateTimeString = date("Y-m-d H:i:s");
		$log = array(
			"userId" => $userId,
			"count" => $count,
			"reason" => $reason,
			"extra" => $extra,
			"dateTime" => $currentDateTimeString
		);
		
		
		
		$firebase->push("coinsAddLogs", $log);
		
	}

?>
