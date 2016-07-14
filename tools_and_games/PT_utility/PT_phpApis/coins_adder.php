<?php

include 'coins_log.php';

	function addCoinToUser($userId, $count, $reason, $extra, $firebase){
		$currentUserCoinsString = $firebase->get("coins/".$userId."/count");
			
		if(empty($currentUserCoinsString)){
			$currentUserCoins = 0;
		}
		else{
			$currentUserCoins = (int)json_decode($currentUserCoinsString);
		}
		
		
		$originalCoins = $currentUserCoins;
		$currentUserCoins += $count;
		$firebase->set("coins/".$userId."/count", $currentUserCoins);
		
		addCoinLog($userId, $originalCoins, $currentUserCoins, $reason, $extra, true, $firebase);
		
	}

?>
