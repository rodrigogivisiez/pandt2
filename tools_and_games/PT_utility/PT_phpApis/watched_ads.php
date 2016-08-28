<?php
	include 'firebaseInterface.php';
	include 'firebaseStub.php';
	include 'firebaseLib.php';
	include 'firebase_details.php';
	include 'coins_adder.php';
	include 'secret_check.php';
	
	if(!checkSecretMatched($_POST["restSecret"])){
		return;
	}
	
	
	date_default_timezone_set("Asia/Singapore");
	
	$secret = "3E4933FD7CDD337AF874E53FD975BECD1F629276CE6CB4346A6F1B2C12E786B59195311647F7D6F3926CA417ED2CE7E4BFB8F2EEA12FE84BF6B3";
	
	$inputSecret = $_POST["secret"];
	
	$userId = $_POST["userId"];
	$firebase = new \Firebase\FirebaseLib($DEFAULT_URL, $DEFAULT_TOKEN);
	
	if($secret === $inputSecret){
		
		$userValid = false;
		$dbUserId = json_decode($firebase->get('users/'.$userId.'/userId'));
		$userValid = ($dbUserId == $userId);
	
		if(!$userValid) return false;
		
		$coinRewarded = (int) json_decode($firebase->get('rewardVideoCoinCount/'));
		
		
		addCoinToUser($userId, $coinRewarded, "Watch Video Ads", "", $firebase);
	}
	

	
?>
