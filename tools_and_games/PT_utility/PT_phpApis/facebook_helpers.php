<?php
	
	
	function getAppAccessToken(){
		$graph_url = "https://graph.facebook.com/oauth/access_token?".
						"grant_type=client_credentials&".
						"client_id=500529570103453&".
						"client_secret=32411ed441f4ebca5f385087933a980a&".
						"redirect_uri=";
							
		$ch = curl_init();
	
        curl_setopt($ch, CURLOPT_URL, $graph_url);
        curl_setopt($ch, CURLOPT_HEADER, 0);
		curl_setopt($ch, CURLOPT_POST, 0);
		curl_setopt($ch, CURLOPT_TIMEOUT, 10);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 10);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
		curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        $output = curl_exec($ch);

        curl_close($ch);
		
		return $output;
	}
	
	
	function isTokenValid($token, $fbUserId){
		$graph_url = "https://graph.facebook.com/v2.6/debug_token?".
						"access_token=500529570103453|UIew2VTxQ9iJ5jQWLyCVd4pNhRA&".
						"input_token=".$token;
							
		$ch = curl_init();
	
        curl_setopt($ch, CURLOPT_URL, $graph_url);
        curl_setopt($ch, CURLOPT_HEADER, 0);
		curl_setopt($ch, CURLOPT_POST, 0);
		curl_setopt($ch, CURLOPT_TIMEOUT, 10);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 10);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
		curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        $output = curl_exec($ch);

        curl_close($ch);
		
		$result = json_decode($output);
		if($result->data->user_id == $fbUserId && $result->data->is_valid == true){
			return true;
		}
		else{
			return false;
		}
		

	}
	
	
?>
