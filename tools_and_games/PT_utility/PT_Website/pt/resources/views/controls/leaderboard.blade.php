
<div class="records">
	@for($i = 0; $i < 15; $i++)
	
	<?php
		if($i <= count($records) - 1){
			$record = $records[$i];
		}
		else{
			$record = null;
		}
	?>
	
	<div class="record">
		<div class="index">
			{{($i + 1)."."}}
		</div>
		<div class="nameScore">
			<div class="country">
				@if(isset($record) && isset($record->profile))
				<img src={{getCountryFlagPath(isset($record->profile->country) ? $record->profile->country : "UNKNOWN")}}>
				@endif
			</div>
			
			@if(isset($record) && isset($record->streakCount) && $record->streakCount >= 2)
			<div class="streak">
				{{$record->streakCount}}
			</div>
			@endif
			
			<div class="name">
				{{(isset($record) && isset($record->profile)) ? $record->profile->gameName : ""}}
			</div>
			
			
			<div class="score">
				@if(isset($record))
					{{number_format($record->score)}}
				@endif
			</div>
			
			@if(isset($record) && count($record->userIds) > 1)
			<?php
				$userIds = array();
				foreach($record->userIds as $userId){
					if($userId != $record->profile->userId){
						array_push($userIds, $userId);
					}
				}
				$userIdsString = implode(",", $userIds);
			?>
			
			<div class="plusFriends noSelect" userIds="{{$userIdsString}}" data-trigger="manual" data-html="true" data-original-title="Loading..">
				{{"+".(count($record->userIds) - 1)}}
			</div>
			@endif
			
			<div class="clear">
				
			</div>
			
			@if($i != 14)
			<div class="separator">
				
			</div>
			@endif
		</div>
		
		<div class="clear">
			
		</div>
		
	</div>
	@endfor
</div>