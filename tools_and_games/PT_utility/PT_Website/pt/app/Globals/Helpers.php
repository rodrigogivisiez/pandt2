<?php

	function timeElapsedString($datetime, $full = false) {
	    $now = new DateTime;
	    $ago = new DateTime($datetime);
	    $diff = $now->diff($ago);
	
	    $diff->w = floor($diff->d / 7);
	    $diff->d -= $diff->w * 7;
	
	    $string = array(
	        'y' => 'year',
	        'm' => 'month',
	        'w' => 'week',
	        'd' => 'day',
	        'h' => 'hour',
	        'i' => 'minute',
	        's' => 'second',
	    );
	    foreach ($string as $k => &$v) {
	        if ($diff->$k) {
	            $v = $diff->$k . ' ' . $v . ($diff->$k > 1 ? 's' : '');
	        } else {
	            unset($string[$k]);
	        }
	    }
	
	    if (!$full) $string = array_slice($string, 0, 1);
	    return $string ? implode(', ', $string) . ' ago' : 'just now';
	}
		
	function getCountryFlagPath($country){
		
		if(empty($country)){
			$country = "UNKNOWN";
		}
		
		$filename = 'css/images/countries/'.$country.".png";
		
		
		
		if (file_exists($filename)) {
		    return $filename;
		} else {
		   return 'css/images/countries/UNKNOWN.png';
		}
		
	}
	
	
?>