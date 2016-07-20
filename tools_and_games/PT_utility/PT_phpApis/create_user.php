<?php
	include 'firebaseInterface.php';
	include 'firebaseStub.php';
	include 'firebaseLib.php';
	include 'firebase_details.php';
	include 'facebook_helpers.php';
	include 'secret_check.php';
	
	function generateRandomString($length = 30) {
	    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
	    $charactersLength = strlen($characters);
	    $randomString = '';
	    for ($i = 0; $i < $length; $i++) {
	        $randomString .= $characters[rand(0, $charactersLength - 1)];
	    }
	    return $randomString;
	}
	
	if(!checkSecretMatched($_POST["restSecret"])){
		echo -1;
		return;
	}
	
	
	$firebase = new \Firebase\FirebaseLib($DEFAULT_URL, $DEFAULT_TOKEN);
	
	$fbUserId = htmlspecialchars($_POST["fbUserId"]);
	$fbUsername = htmlspecialchars($_POST["fbUsername"]);
	$fbToken = htmlspecialchars($_POST["fbToken"]);
	$createNewUser = false;
	$fbTokenMatch;
	
	if(!empty($fbUserId) && !empty($fbToken)){
		if(isTokenValid($fbToken, $fbUserId)){
			$fbTokenMatch = true;
			$userId = json_decode($firebase->get('secret/facebookUserIds/'.$fbUserId));
			if(empty($userId)){
				$createNewUser = true;
			}
			else{
				$response = $firebase->get('secret/users/'.$userId);
				$result = json_decode($response);
				$toReturn = new stdClass();
				$toReturn->userId = $result->userId;
				$toReturn->secret = $result->secret;
				
				echo json_encode($toReturn);
				return;
			}
		}
		else{
			echo -1;
			return;
		}
	}
	else{
		$createNewUser = true;
	}
	
	if($createNewUser){
		$dummy = array(
		    "gameNameLower" => "",
		);
	
		
		$response = $firebase->push("secret/users", $dummy); 
		$result = json_decode($response);
		
		$randomString = generateRandomString();
		
		$userIdSecretMap = array(
		    "userId" => $result->name,
		    "secret" => $randomString
		);
		
		$firebase->set("secret/users/".$result->name, $userIdSecretMap); 
		
		
		$facebookInfoMap = array(
			"userId" => $result->name,
		);
		if($fbTokenMatch){
			$facebookInfoMap["facebookUserId"] = $fbUserId;
			$facebookInfoMap["facebookName"] = $fbUsername;
			$firebase->set("secret/facebookUserIds/".$fbUserId, $result->name); 
		}
		
		
		$firebase->set("users/".$result->name, $facebookInfoMap); 
	
		$toReturn = new stdClass();
		$toReturn->userId = $result->name;
		$toReturn->secret = $randomString;
		
		echo json_encode($toReturn);
		return;
	}
	else{
		echo -1;
		return;
	}
	
	
	
?>
