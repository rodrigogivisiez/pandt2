<?php

include 'coins_log.php';


	function decreaseCoinFromUser($userId, $count, $checkOrActualDecrease, $reason, $extra, $firebase){
		
		if($count == 0) return true;
		
		$currentUserCoinsString = $firebase->get("coins/".$userId."/count");
			
		if(empty($currentUserCoinsString)){
			return false;
		}
		else{
			$currentUserCoins = (int)json_decode($currentUserCoinsString);
		}
		
		$originalCoins = $currentUserCoins;
		$currentUserCoins -= $count;
		
		if($checkOrActualDecrease == 0){
			if($currentUserCoins < 0){
				return false;
			}
			else{
				return true;
			}
		}
		else{
			if($currentUserCoins <= 0){
				$currentUserCoins = 0;
			}
			
			$firebase->set("coins/".$userId."/count", (string) $currentUserCoins);
			
			addCoinLog($userId, $originalCoins, $currentUserCoins, $reason, $extra, false, $firebase);
			
		}
	}

?>
