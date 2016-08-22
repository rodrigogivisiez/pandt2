var player;

function onYouTubeIframeAPIReady() {
    player = new YT.Player('youtubePlayer', {
        videoId: 'HU6FOQpsAK8',
        playerVars: {
            color: 'white',
        },
        events: {
            onReady: initialize
        }
    });
}

function initialize(){

}


$(".home .video").click(function(){
	$(".home .youtubeContainer").show();
	setTimeout(func, 10);
	
});



function func() {
   $("body").scrollTo(".home .youtubeContainer", 500, {offset: {top:70, left:0}});
	player.playVideo();
}
