<?php
	include 'firebaseInterface.php';
	include 'firebaseStub.php';
	include 'firebaseLib.php';
	include 'firebase_details.php';
	
	date_default_timezone_set("Asia/Singapore");
	
	function getRetriveableCoinData($diffInSecs){
		$secsPerCoin = 7200;		//7200 secs per coin
		$limit = 5;
			
		$result = new stdClass();
		$result->canRetrieveCoinsCount = floor($diffInSecs / $secsPerCoin);
		
		if($result->canRetrieveCoinsCount >= $limit){
			$result->canRetrieveCoinsCount = $limit;
			$result->nextCoinInSecs = 0;
		}
		else{
			$result->nextCoinInSecs = $secsPerCoin - $diffInSecs % $secsPerCoin;
		}
		
		$result->maxRetrieveableCoins = $limit;
		$result->secsPerCoin = $secsPerCoin;
		return $result;
	}

	$retrieveRequest = $_POST["retrieveRequest"];
	$userId = $_POST["userId"];
	$userToken = $_POST["userToken"];
	
	if(empty($userId)){
		$userId = "1";
	}
	
	$firebase = new \Firebase\FirebaseLib($DEFAULT_URL, $DEFAULT_TOKEN);
	
   	$userValid = false;
	$dbToken = json_decode($firebase->get('secret/users/'.$userId.'/token'));
	$userValid = ($dbToken == $userToken);
	

	if(!$userValid) return false;
	
	
	$currentDateTimeString = date("Y-m-d H:i:s");
	$currentDateTime = new DateTime($currentDateTimeString);
	
	
	$lastRetriveDateTimeString = $firebase->get("coinsLastRetrieved/".$userId);
	if(!empty($lastRetriveDateTimeString)){
		$lastRetriveDateTimeString = json_decode($lastRetriveDateTimeString);
	}
	
	$diffInSeconds = 99999999;
	
	if(!is_null($lastRetriveDateTimeString) && $lastRetriveDateTimeString != "null"){
		$lastRetriveDateTime = new DateTime($lastRetriveDateTimeString);
		
		$diffInSeconds = $currentDateTime->getTimestamp() - $lastRetriveDateTime->getTimestamp();
	}
	
	$resultData = getRetriveableCoinData($diffInSeconds);
	
	if(isset($retrieveRequest) && $retrieveRequest != "0"){		//retrive coins
		if($resultData -> canRetrieveCoinsCount > 0){
			$currentUserCoinsString = $firebase->get("coins/".$userId);
			
			if(empty($currentUserCoinsString)){
				$currentUserCoins = 0;
			}
			else{
				$currentUserCoins = (int)json_decode($currentUserCoinsString);
			}
			
			$currentUserCoins += $resultData -> canRetrieveCoinsCount;
		
			$firebase->set("coinsLastRetrieved/".$userId, $currentDateTimeString);
			$firebase->set("coins/".$userId, $currentUserCoins);
			echo json_encode(getRetriveableCoinData(0));
		}
	}
	else{
		echo json_encode($resultData);
	}	
?>
