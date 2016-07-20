<?php
	include 'firebaseInterface.php';
	include 'firebaseStub.php';
	include 'firebaseLib.php';
	include 'firebase_details.php';
	include 'google_iab_helpers.php';
	include 'coins_adder.php';
	include 'secret_check.php';
	
	
	date_default_timezone_set("Asia/Singapore");
	
	
	if(!checkSecretMatched($_POST["restSecret"])){
		echo -1;
		return;
	}
	
	$phase = $_POST["phase"];
	$userId = $_POST["userId"];
	$userToken = $_POST["userToken"];
	$productId = $_POST["productId"];
	$productToken = $_POST["productToken"];
	$orderId = $_POST["orderId"];
	
	$firebase = new \Firebase\FirebaseLib($DEFAULT_URL, $DEFAULT_TOKEN);
	
	$userValid = false;
	$dbToken = json_decode($firebase->get('secret/users/'.$userId.'/token'));
	$userValid = ($dbToken == $userToken);
	
	if(!$userValid) return false;
	
	$resultJson = getPurchaseDetails($productId, $productToken, 0, $firebase);
	
	$encodeProductToken = str_replace("%", "!", str_replace(".", "%2E", urlencode($productToken)));		//% cause invalid token in firebase set
	
	if($resultJson == -1){
		echo "-1";
	}
	else{
		$purchaseDetails = json_decode($resultJson);
		if($phase == "0"){
			if($purchaseDetails->purchaseState == 0 && $purchaseDetails->consumptionState == 0){
				$firebase->set('secret/purchases/'.$encodeProductToken, null);
				echo "0";
			}
			else{
				echo "-1";
			}
		}
		else if($phase == "1"){
			if($purchaseDetails->purchaseState == 0 && $purchaseDetails->consumptionState == 1){
				if(empty($productToken)) $encodeProductToken = "1";
				
				$exist = json_decode($firebase->get('secret/purchases/'.$encodeProductToken));
				if(empty($exist)){
					$product = json_decode($firebase->get('coinsProducts/'.$productId));
					if(!empty($product)){	
						$firebase->set('secret/purchases/'.$encodeProductToken, $userId);
						addCoinToUser($userId, $product->count, "Purchase coin", "Product Id: ".$productId. ", Product Token: ".$productToken. ", Order Id: ".$orderId, $firebase);
						
						echo "0";
					}
					else{
						echo "-1";
					}
				}
				else{
					echo "-1";
				}
			}
			else{
				echo "-1";
			}
		}
	}
	

	
?>
