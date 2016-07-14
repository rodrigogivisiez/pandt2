<?php
	include 'firebaseInterface.php';
	include 'firebaseStub.php';
	include 'firebaseLib.php';
	include 'firebase_details.php';
	include 'coins_decreaser.php';
	
	
	function checkAlreadyUpdate($firebase, $transactionId){
		$updated = json_decode($firebase->get('coinsTransactionHistories/'.$transactionId));
		if($updated == 1){
			return true;
		}
		else{
			$firebase->set('coinsTransactionHistories/'.$transactionId, 1);
			return false;
		}
	}
	
	function checkAgreementSigned($firebase, $userId, $transactionId){
		$updated = json_decode($firebase->get('coinDecreaseAgreements/'.$userId.'/'.$transactionId));
		if($updated == 1){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	$coinsMetaJson = $_POST["coinsMetaJson"];
	$transactionId = $_POST["transactionId"];
	$expectingCoins = $_POST["expectingCoins"];
	$purpose = $_POST["purpose"];

	
	$firebase = new \Firebase\FirebaseLib($DEFAULT_URL, $DEFAULT_TOKEN);
	
	
	
	if(empty($expectingCoins) || $expectingCoins <= 0){
		echo "-1";
	}
	else{
		if(checkAlreadyUpdate($firebase, $transactionId)){
			echo "0";
			return;
		}
		
		
		$totalCoinsReceived = 0;
		$coinsMeta = json_decode($coinsMetaJson);
		
		foreach($coinsMeta as $meta){
			$totalCoinsReceived += $meta->coinsCount;
		}
		
		if($totalCoinsReceived < $expectingCoins){
			echo "-1";
			return;
		}
		
		foreach($coinsMeta as $meta){
			if(!checkAgreementSigned($firebase, $meta->userId, $transactionId) || !decreaseCoinFromUser($meta->userId, $meta->coinsCount, 0, "", "", $firebase)){
				$canceled = true;
				break;
			}
		}
		
		if($canceled){
			echo "-1";
			return;
		}
		
		
		foreach($coinsMeta as $meta){
			decreaseCoinFromUser($meta->userId, $meta->coinsCount, 1, $purpose, "", $firebase);
		}
		
		echo "0";

		
	}
	

?>
