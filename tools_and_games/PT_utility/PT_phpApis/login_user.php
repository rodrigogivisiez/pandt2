<?php
	use Firebase\Token\TokenException;
	use Firebase\Token\TokenGenerator;
	include 'firebaseInterface.php';
	include 'firebaseStub.php';
	include 'firebaseLib.php';
	include 'firebase_details.php';
	include 'BeforeValidException.php';
	include 'ExpiredException.php';
	include 'JWT.php';
	include 'SignatureInvalidException.php';
	include 'TokenException.php';
	include 'TokenGenerator.php';
	
	try {
		
		
		$userId = htmlspecialchars($_POST["userId"]);
		$secret = htmlspecialchars($_POST["secret"]);
		
		
		$firebase = new \Firebase\FirebaseLib($DEFAULT_URL, $DEFAULT_TOKEN);
		$response = $firebase->get('secret/users/'.$userId);
		$result = json_decode($response);
		if(!empty($result->secret) && $result->secret == $secret){
			$isModerator = false;
			
			if(!empty($result->isModerator)){
				$isModerator = $result->isModerator;
			} 
			
			$generator = new TokenGenerator($DEFAULT_TOKEN);
		    $token = $generator
		        ->setData(array('uid' => $userId,
								"isModerator" => $isModerator))
		        ->create();
			
			$firebase->set('secret/users/'.$userId."/token", $token);
			
			echo $token;
			return;
		}
		else{
			echo "USER_NOT_FOUND";
			return;
		}
		
	
	} catch (TokenException $e) {
		
	}
	
	
	echo -1;
	return;
	
?>
