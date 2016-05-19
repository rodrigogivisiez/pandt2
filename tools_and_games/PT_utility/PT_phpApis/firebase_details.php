<?php
	
	if (strpos($_SERVER['HTTP_HOST'], 'localhost') !== false) // or any other host
	{
	     $DEFAULT_URL = 'https://ptapptest.firebaseio.com/';
		 $DEFAULT_TOKEN = 'xU62Y6naxtpRUZZad429zIPu7f3rSVcVrjG4MOMp';
	}
	
	else
	{
		 $DEFAULT_URL = 'https://glaring-inferno-8572.firebaseio.com/';
	     $DEFAULT_TOKEN = "UogxKt0DL9RgHnadZ3nmcrPwJQBT3b699vjMOpPO";
	}
	
?>
