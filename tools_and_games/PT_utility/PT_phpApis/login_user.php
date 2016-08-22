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
	include 'facebook_helpers.php';
	include 'secret_check.php';
	
	
	
	
	function getIpCountry()
	{
	
	    $client  = @$_SERVER['HTTP_CLIENT_IP'];
	    $forward = @$_SERVER['HTTP_X_FORWARDED_FOR'];
	    $remote  = $_SERVER['REMOTE_ADDR'];
	    $country  = "Unknown";
	
	    if(filter_var($client, FILTER_VALIDATE_IP))
	    {
	        $ip = $client;
	    }
	    elseif(filter_var($forward, FILTER_VALIDATE_IP))
	    {
	        $ip = $forward;
	    }
	    else
	    {
	        $ip = $remote;
	    }
	    $ch = curl_init();
	    curl_setopt($ch, CURLOPT_URL, "http://www.geoplugin.net/json.gp?ip=".$ip);
	    curl_setopt($ch, CURLOPT_HEADER, 0);
	    curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
	    $ip_data_in = curl_exec($ch); // string
	    curl_close($ch);
	
	    $ip_data = json_decode($ip_data_in,true);
	    $ip_data = str_replace('&quot;', '"', $ip_data); // for PHP 5.2 see stackoverflow.com/questions/3110487/
	
	    if($ip_data && $ip_data['geoplugin_countryCode'] != null) {
	        $country = $ip_data['geoplugin_countryCode'];
	    }
	
	    return $country;
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	if(!checkSecretMatched($_POST["restSecret"])){
		echo -1;
		return;
	}
	
	try {
		
		
		$userId = htmlspecialchars($_POST["userId"]);
		$secret = htmlspecialchars($_POST["secret"]);
		$fbUserId = htmlspecialchars($_POST["fbUserId"]);
		$fbToken = htmlspecialchars($_POST["fbToken"]);
		
		$firebase = new \Firebase\FirebaseLib($DEFAULT_URL, $DEFAULT_TOKEN);
		
		if(!empty($fbUserId) && !empty($fbToken)){
			if(isTokenValid($fbToken, $fbUserId)){
				$fbTokenMatch = true;
				$userId = json_decode($firebase->get('secret/facebookUserIds/'.$fbUserId));
				if(empty($userId)){
					echo "USER_NOT_FOUND";
					return;
				}
				else{
					$response = $firebase->get('secret/users/'.$userId);
					if(empty($response)){
						echo "USER_NOT_FOUND";
						return;
					}
					$result = json_decode($response);
					$secret = $result->secret;
				}
			}
			else{
				echo -1;
				return;
			}
		}
			
		
		
		if(empty($userId)) $userId = 1;
		$response = $firebase->get('secret/users/'.$userId);
		
		if($response == false){
			echo "FAIL_CONNECT";
			return;
		}
			
		$result = json_decode($response);
		if(!empty($result->error)){
			echo "FAIL_CONNECT";
			return;
		}
		
			
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
			
			
			$toReturn = new stdClass();
			$toReturn->userId = $userId;
			$toReturn->secret = $secret;
			$toReturn->token = $token;
			$toReturn->country = getIpCountry();
			
			echo json_encode($toReturn);
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
