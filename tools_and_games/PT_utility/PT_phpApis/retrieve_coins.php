<?php
	include 'firebaseInterface.php';
	include 'firebaseStub.php';
	include 'firebaseLib.php';
	include 'firebase_details.php';
	include 'coins_adder.php';
	include 'secret_check.php';
	
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

	if(!checkSecretMatched($_POST["restSecret"])){
		echo -1;
		return;
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
			
			$firebase->set("coinsLastRetrieved/".$userId, $currentDateTimeString);
			addCoinToUser($userId, $resultData -> canRetrieveCoinsCount, "Mum Purse", "", $firebase);
			
			echo json_encode(getRetriveableCoinData(0));
		}
	}
	else{
		echo json_encode($resultData);
	}	
?>
