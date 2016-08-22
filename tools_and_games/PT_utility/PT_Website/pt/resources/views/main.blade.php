<!DOCTYPE html>
<html lang="en">

<head>
	<base href="{{URL::to('/')}}/" target="_top">
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="robots" content="index, follow">
	<meta name="description" content="sample description">
	
	<link rel="shortcut icon" href="public/css/images/favicon.ico" type="image/x-icon" /> 
	<link rel="stylesheet" href="css/bootstrap.css">
	<link rel="stylesheet" href="css/simplebar.css">
	<link rel="stylesheet" href="css/share.css">
	<link rel="stylesheet" href="css/header.css">
	<link rel="stylesheet" href="css/home.css">
	<link rel="stylesheet" href="css/games.css">
	<link rel="stylesheet" href="css/support.css">
	<link rel="stylesheet" href="css/footer.css">
	
	<title>
		<?php
			$base = "Potato and Tomato - Multiplayer Mobile Game";
			if($page == "home"){
				echo $base;
			}
			elseif($page == "games"){
				echo "Our Games | ".$base;
			}
			elseif($page == "faq"){
				echo "FAQ | ".$base;
			}
			elseif($page == "terms"){
				echo "Terms of Service | ".$base;
			}
			elseif($page == "privacy"){
				echo "Privacy Policy | ".$base;
			}
			elseif($page == "support"){
				echo "Support | ".$base;
			}
		?>
		</title>	
</head>
<body>
	
	@include('controls.header')
	
	<div class="container-fluid background">
		
	</div>
	
	<div class="pageContainer">
		@include('pages.'.$page)
	</div>
	
	
	
	@include('controls.footer')
	
	<!-- Scripts -->
	<script type="text/javascript" src="js/jquery.min.js"></script>
	<script type="text/javascript" src="js/simplebar.min.js"></script>
	<script type="text/javascript" src="js/jquery-bootstrap.min.js"></script>
	<script type="text/javascript" src="js/jquery.scrollTo.min.js"></script>
	<script type="text/javascript" src="js/home.js"></script>
	<script type="text/javascript" src="js/games.js"></script>
	
	
</body>
</html>
