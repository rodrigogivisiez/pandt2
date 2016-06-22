<?php
	
	
	
	function getPurchaseDetails($productId, $productToken, $count, $firebase){
		
		if(empty($productId) || empty($productToken) || $count >= 3) return -1;
		
		$accessToken = json_decode($firebase->get('secret/accessToken/token'));
		if(empty($accessToken)){
			$accessToken = refreshAccessToken();
			$firebase->set('secret/accessToken/token', $accessToken);
		}
		
		
		$url  = "https://www.googleapis.com/androidpublisher/v2/applications/com.mygdx.potatoandtomato.android/purchases/products/".
					  $productId."/tokens/".
					  $productToken."/?access_token=".$accessToken;
							
		$ch = curl_init();
	
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_HEADER, 0);
		curl_setopt($ch, CURLOPT_POST, 0);
		curl_setopt($ch, CURLOPT_TIMEOUT, 10);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 10);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
		curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        $output = curl_exec($ch);

        curl_close($ch);
		
		$decoded = json_decode($output);
		
		if(isset($decoded->error->code)){
			if($decoded->error->code == 401){
				$accessToken = refreshAccessToken();
				$firebase->set('secret/accessToken/token', $accessToken);
				return getPurchaseDetails($productId, $productToken, $count + 1);
			}
			else{
				return -1;
			}	
		}
		else{
			if(isset($decoded->consumptionState)){
				return $output;
			}
			else{
				return -1;
			}
		}	
	}
	
	function refreshAccessToken(){
		$url  = "https://accounts.google.com/o/oauth2/token";
							
		$post = [
				    'grant_type' => 'refresh_token',
				    'client_id' => '122927998478-mqbnen7vokss7clk05jqi3sm3tnu9lic.apps.googleusercontent.com',
				    'client_secret'   => 'tUyr-RK0l2o1f_y55aE96CZJ',
				    'refresh_token' => '1/Y0s4MklHc5ec6ZR5YNn4NTyrLszusAkLmqFqMQ_-7wk'
				];
			
		$ch = curl_init($url);
		curl_setopt($ch, CURLOPT_POST, 1);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		curl_setopt($ch, CURLOPT_TIMEOUT, 10);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 10);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
		curl_setopt($ch, CURLOPT_POSTFIELDS, $post);
		
		// execute!
		$output = curl_exec($ch);
		
		// close the connection, release resources used
		curl_close($ch);
		
		$decoded = json_decode($output);
		
		$accessToken = $decoded->access_token;
		
		return $accessToken;
	}
	
	
	
?>
