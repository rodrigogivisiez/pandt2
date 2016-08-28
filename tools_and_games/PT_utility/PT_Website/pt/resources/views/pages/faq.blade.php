<title></title>

<?php 

	$arr = array();
	
	$obj = new stdClass;
	$obj->question = "What is Potato and Tomato?";
	$obj->answer = "We are a platform that hosts games for you to play, multiplayer style, with your friends!";
	
	array_push($arr, $obj);
	
	$obj = new stdClass;
	$obj->question = "How are you different from some other multi-player apps? ";
	$obj->answer = "We will host multiple games in one lightweight app that comes with an easy in-game lobby for you to play with your friends. Our mission is to connect you with your friends or new friends to build memories together!";
	
	array_push($arr, $obj);
	
	$obj = new stdClass;
	$obj->question = "How do I get my friends to join to play the game? ";
	$obj->answer = "First, they will have to download the app, available at 
	<br><a href='https://play.google.com/store/apps/details?id=com.mygdx.potatoandtomato.android'>https://play.google.com/store/apps/details?id=com.mygdx.potatoandtomato.android</a>. 
	<br>After that, there are 2 options for them to join the game, 
<br><br>
Facebook Sign-in - You will be able to invite them after clicking on our invite button followed by the Facebook tab if they have downloaded the app and signed in with Facebook. 
<br><br>
In-game lobby - If either of you has not signed in using Facebook, you will be able to create the game as a host first. The game room will pop up in the Game List under your game name. They will be able to join that game room then using the Join Game button. Next time, you can invite them as recently played friends under the first tab after clicking the Invite Button! 
";
	
	array_push($arr, $obj);
	
	$obj = new stdClass;
	$obj->question = "Why do I need to sign into Facebook account?";
	$obj->answer = "Facebook sign in is purely optional on P&T. You will, however, not be able to invite your Facebook friends who have installed the app into the game. Also, when you switch devices, you will not be able to retain the account and your current coin purse. Hence, Facebook sign in is highly recommended. Potato and Tomato studio will not post anything on your behalf on your Facebook wall. 
";
	
	array_push($arr, $obj);
	
	
	$obj = new stdClass;
	$obj->question = "Is Potato and Tomato free to play? ";
	$obj->answer = "It is free to download, free to play! Every day, we provide a certain number of coins for you to play the games at Mum’s purse. When you run out of coin, you can always watch a sponsored video or purchase more coins in our shop. Your support is greatly welcomed as it helps us to continue to produce more multiplayer games for you! 
";
	
	array_push($arr, $obj);
	
	$obj = new stdClass;
	$obj->question = "How do I raise any bug or suggestion for improvement as a user? ";
	$obj->answer = "Use our <a href='support'>support</a>! We welcome any suggestion or bug issue and we greatly appreciate your patience as we will need some time to revert to all of your queries.  
";
	
	array_push($arr, $obj);
	
	$obj = new stdClass;
	$obj->question = "More information about you? ";
	$obj->answer = "You can find more of us here at <a href='presskit'>presskit</a>";
	
	array_push($arr, $obj);
	
	$obj = new stdClass;
	$obj->question = "What are your term of services and privacy policy? ";
	$obj->answer = "They are available <a href='terms'>here</a> and <a href='privacy'>here</a>. ";
	
	array_push($arr, $obj);
	
	$obj = new stdClass;
	$obj->question = "What will you use my personal information for? ";
	$obj->answer = "P&T collects information mainly for customer analytics purposes and to enable certain features like country flags on leaderboard. We do not collect identifiable information and we certainly do not post anything on you Facebook wall without your prior consent. Please refer to our privacy policy for more information on how we will use your information. 
";
	
	array_push($arr, $obj);
	
	$obj = new stdClass;
	$obj->question = "Are you available on iOS? ";
	$obj->answer = "No, unfortunately we don’t have a version for iOS for now. ";
	
	array_push($arr, $obj);

	$obj = new stdClass;
	$obj->question = "One user has been using abusive language. Can I get him/her banned? ";
	$obj->answer = "Yes, please report the user’s in game name via support. We are a new community and we do not tolerate abusive or inappropriate language used against our users. We will investigate each case and take appropriate action as soon as possible. 
	";
	
	array_push($arr, $obj);
	
	$obj = new stdClass;
	$obj->question = "Can I play offline? ";
	$obj->answer = "You need to be online to access Potato and Tomato, as we are an app to connect to your friends. We have however one-player mode for Photo Mania Resistance and also we are in the process of creating a bot for Behind you a jungle! 
";
	
	array_push($arr, $obj);
	
	$obj = new stdClass;
	$obj->question = "Can I reconnect back after losing my connection briefly?  ";
	$obj->answer = "We allow 60 seconds for the users to reconnect before we will drop the user from the game, you may reconnect back by using the Continue button in games list screen. We highly recommend having stable Mobile Data or Wifi connection to have a smooth gaming experience. 
 ";
	
	array_push($arr, $obj);
	
	$obj = new stdClass;
	$obj->question = "I can't seem to reconnect back to my original game after losing connection. Why? ";
	$obj->answer = "There are several reasons why this is the case. 
	<br>1) You have lost connection to Internet
	<br>2) You have failed to reconnect back to the game within 60 seconds
	<br>3) You have signed into restricted wifi network that does not allow you to connect to us
	<br>4) The game has ended as your friends have left or get disconnected from the game   
 ";
	
	array_push($arr, $obj);
	
	
	
	$obj = new stdClass;
	$obj->question = "Can I turn off auto played audio message?";
	$obj->answer = "Yes, please do it at setting.";
	
	array_push($arr, $obj);
	
	
	$obj = new stdClass;
	$obj->question = "Can I change my on-screen name? ";
	$obj->answer = "Yes, you can do it at setting. Your recently played history with other friends however will still show your old on-screen name and will only get updated after you have played with your friends again. ";
	
	array_push($arr, $obj);
	
?>





























<div class="container faq">
	
	@foreach($arr as $obj)
	<div class="row faqItem">
		
		<div class="col-xs-12 question">
			<h3>&bull; {{$obj->question}}</h3>
		</div>
		
		<div class="col-xs-12 answer">
			<h4>{!!$obj->answer!!}</h4>
		</div>
		
	</div>
	@endforeach
	
	
</div>


















