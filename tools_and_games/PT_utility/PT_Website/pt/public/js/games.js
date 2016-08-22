$('body').on('click', function (e) {
    //did not click a popover toggle or popover
    if (!$(e.target).hasClass("plusFriends") 
        && $('.record .plusFriends.active').length > 0) { 
        $('.record .plusFriends.active').removeClass("active").tooltip('hide');
    }
});

$('body').tooltip({
    selector: '.record .plusFriends',
    trigger:"manual"
});


$(document).ready(function() {
    function loop(left) {
    	var operator = "-";
    	if(!left){
    		operator = "+";
    	}
    	
    	if($('.pickGame img').length == 0) return;
    	
        $('.pickGame img').animate ({
            marginLeft: operator + '=20',
        }, 500, 'linear', function() {
            loop(!left);
        });
    }
    loop(true);
});



$(".leaderboardRecords" ).on( "click", ".plusFriends", function() {
  	var $this = $(this);
	var isActive = $this.hasClass("active");
	
	$(".record .plusFriends.active").tooltip("hide").removeClass("active");
	
	if(!isActive){
		$(this).addClass("active");
		$(this).tooltip("show");
	}
	
	if($this.attr("data-original-title") == "Loading.."){
		$.post("userDetails", {userIdsString: $this.attr("userIds")},function(html){
			if($this.hasClass("active")){
				$this.attr('data-original-title', html).tooltip('show');
			}
			else{
				$this.attr('data-original-title', html);
			}
			
		});
	}
});


$(".games .gameIconsContainer .gameIcon").click(function(){
	
	var abbr = $(this).attr("gameAbbr");
	$('.simpleBar').simplebar('recalculate');
	
	$(".carousel-inner").html($("." + abbr + " .screenshots").html());
	$(".carousel").show();
	$(".carousel").carousel("pause").removeData();
	
	$(".pickGame").remove();
	$(".simpleBar").show();
	
	var gameName = $(this).attr("alt");
	$(".detailsContainer .gameDetails").hide();
	$(".leaderboardRecords .recordsContainer").hide();
	
	$(".detailsContainer ." + abbr).fadeIn();
	
	var $recordsContainer = $(".leaderboardRecords ." + abbr);
	var hasStreaks = ($recordsContainer.attr("streakEnabled") == "1");
	var gameAbbr = $recordsContainer.attr("gameAbbr");
	$recordsContainer.fadeIn();
	
	if($recordsContainer.attr("retrieved") == "false"){
		$.post("leaderboard", {game: gameAbbr, streakEnabled: hasStreaks},function(html){
			$recordsContainer.html(html).attr("retrieved", "true");
		});
	}
	
	$(".leaderboard .title").text(gameName + " Leaderboard");
});




















