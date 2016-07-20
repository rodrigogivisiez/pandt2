<?php
	include 'firebaseInterface.php';
	include 'firebaseStub.php';
	include 'firebaseLib.php';
	include 'firebase_details.php';
	include 'coins_decreaser.php';
	include 'secret_check.php';
	
	
	if(!checkSecretMatched($_POST["restSecret"])){
		echo -1;
		return;
	}
	
	$teamUserIdsString = $_POST["teamUserIdsString"];
	$coinsMetaJson = $_POST["coinsMetaJson"];
	$gameAbbr = $_POST["gameAbbr"];
	$roomId = $_POST["roomId"];
	$roundCounter = $_POST["roundCounter"];
	
	
	$firebase = new \Firebase\FirebaseLib($DEFAULT_URL, $DEFAULT_TOKEN);
	
	$revived = json_decode($firebase->get('streaksRevived/'.$roomId.'/'.$roundCounter.'/'.$teamUserIdsString));
	if(!empty($revived)){
		echo "0";
	}
	else{
		$totalCoinsReceived = 0;
		$coinsMeta = json_decode($coinsMetaJson);
		
		foreach($coinsMeta as $meta){
			$totalCoinsReceived += $meta->coinsCount;
		}
		
		$originalStreak = json_decode($firebase->get('streaks/'.$gameAbbr.'/'.$teamUserIdsString));
		
		if(empty($originalStreak)){
			echo "0";
		}
		else{
			if($totalCoinsReceived < floor($originalStreak->beforeStreakCount/2)){
				echo "-1";
			}
			else{
				$canceled = false;
				
				foreach($coinsMeta as $meta){
					
					if(!decreaseCoinFromUser($meta->userId, $meta->coinsCount, 0, "", "", $firebase)){
						$canceled = true;
						break;
					}
				}
				
				if($canceled){
					echo "-1";
				}
				else{
					date_default_timezone_set("Asia/Singapore");
					$currentDateTimeString = date("Y-m-d H:i:s");
					
					$firebase->set('streaksRevived/'.$roomId.'/'.$roundCounter.'/'.$teamUserIdsString, $currentDateTimeString);
					
					
					if($originalStreak->streakCount > 0){	//streak never killed before
						echo "0";
						return;
					}
					
					foreach($coinsMeta as $meta){
						decreaseCoinFromUser($meta->userId, $meta->coinsCount, 1, "Revive streaks", $originalStreak->beforeStreakCount, $firebase);
					}
					
					$streakMap = array(
		   				"streakCount" => $originalStreak->beforeStreakCount,
					    "beforeStreakCount" => 0
					);
					
					$firebase->set('streaks/'.$gameAbbr.'/'.$teamUserIdsString, $streakMap);
					
					echo "0";
				}
				
			}
		}
		
	}
	

?>
