<div class="container games">
	
	<div class="row">
		
		<div class="col-lg-3 col-md-2">
			<div class="row">
				<div class="col-xs-12 gameIconsContainer">
					@foreach($games as $game)
						<div class="gameIconContainer">
							<img alt="{{$game->name}}" 
									class="gameIcon" src="{{$game->iconUrl}}" gameAbbr="{{$game->abbr}}">
						</div>
					@endforeach
				</div>
			</div>		
				
			
			<div class="scratchSeparator hidden-sm hidden-xs">
				
			</div>
			
		</div>
		
		
		<div class="col-lg-5 col-md-5 detailsContainer">
			
			<div class="pickGame">
				<img class="hidden-sm hidden-xs" src="css/images/point_left.png">
				<div>
					<span>Pick A Game</span>
				</div>
			</div>
			
			
			<div class="simpleBar invi" data-simplebar-direction="vertical"">
				@foreach($games as $game)
				<div class="gameDetails {{$game->abbr}}">
					
					<div class="row">
						<div class="col-xs-12 title textCenter">
							<span>
								{{$game->name}}
							</span>
						</div>
					</div>
					
					<?php 
						$details = array();
						
						$detail = new stdClass;
						$detail->title = "Players";
						$detail->content = "From ".$game->minPlayers." to ".$game->maxPlayers. " players";
						
						array_push($details, $detail);
						
						$detail = new stdClass;
						$detail->title = "Version";
						$detail->content = $game->version;
						
						array_push($details, $detail);
						
						$detail = new stdClass;
						$detail->title = "Last Updated";
						$detail->content = timeElapsedString('@'.$game->lastUpdatedTimestamp);
						
						array_push($details, $detail);
						
						$detail = new stdClass;
						$detail->title = "Game Size";
						$detail->content = round($game->gameSize / (1000 * 1000), 2). " Mb";
						
						array_push($details, $detail);
						
						$detail = new stdClass;
						$detail->title = "Description";
						$detail->content = $game->description;
						
						array_push($details, $detail);
						
						
					?>			
					
					@foreach($details as $detail)
					<div class="row detail">
						<div class="col-lg-4 detail-title">
							<span>
								{{$detail->title}}
							</span>
						</div>
						<div class="col-lg-8">
							<span>
								{{$detail->content}}
							</span>
						</div>
					</div>
					@endforeach
					
					<div class="row detail">
						<div class="col-xs-12">
							<div class="row">
								<div class="col-xs-12 detail-title">
									<span>
										Screenshots
									</span>
								</div>
							</div>
							<div class="row">
								<div class="col-xs-12 screenshots invi">
									
									<?php
										$basePath = "css/images/". $game->abbr;
										$pathes = array();
										
										if ($handle = opendir($basePath)) {
										    while (false !== ($entry = readdir($handle))) {
										        if ($entry != "." && $entry != "..") {
													array_push($pathes, $basePath."/".$entry);
										        }
										    }
										    closedir($handle);
										}
									?>
									
									<?php $q = 0?>
									@foreach($pathes as $path)
									 <div class="item <?= $q == 0 ? "active" : ""?>">
										<a href="{{$path}}" target="_blank">
											<img class="screenshot" src="{{$path}}">
										</a>
									 </div>
									<?php $q++;?>
									@endforeach
									
								</div>
							</div>
							
						</div>
					</div>
				
				</div>
				@endforeach
				
				<div class="row">
					<div class="col-xs-12">
						<div id="carousel-example-generic" class="carousel slide col-xs-12 invi" data-ride="carousel">
						  <!-- Wrapper for slides -->
						  <div class="carousel-inner textCenter" role="listbox">
						    
						  </div>
						
						  <!-- Controls -->
						  <a class="left carousel-control" href="#carousel-example-generic" role="button" data-slide="prev">
						    <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
						    <span class="sr-only">Previous</span>
						  </a>
						  <a class="right carousel-control" href="#carousel-example-generic" role="button" data-slide="next">
						    <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
						    <span class="sr-only">Next</span>
						  </a>
						</div>
					</div>
				</div>
			</div>
			
			
			
			
			
			
			
			<div class="scratchSeparator hidden-sm hidden-xs">
				
			</div>
			
			
			
		</div>
		
		
		
		<div class="col-lg-4 col-md-5 leaderboardContainer textCenter">
			<div class="leaderboard">
				<div class="title">
					
				</div>
				
				<div class="leaderboardRecords">
					@foreach($games as $game)
					<div class="recordsContainer {{$game->abbr}}" gameAbbr="{{$game->abbr}}" streakEnabled="{{$game->streakEnabled ? '1' : '0'}}" retrieved="false">
						@if($game->leaderboardType == "None")
							<span class="message">Leaderboard is disabled for this game.</span>
						@else
							<span class="message">Loading...</span>
						@endif
					</div>
					@endforeach
				</div>
			</div>
		</div>
		
		
	</div>
	
	
	
	
</div>