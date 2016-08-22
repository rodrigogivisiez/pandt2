<?php namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use App\Http\Requests;
use Request;

class MainController extends Controller {

	  
	public function PAGE_root(){
		return view('main')
				->with("page", "home");
	}
	
		
	public function PAGE_games(){
		
		$data = \Firebase::get('/gamesSimple');
		$games = json_decode($data);
		
		return view('main')
				->with("page", "games")
				->with("games", $games);
	}
	
	public function PAGE_faq(){
		return view('main')
				->with("page", "faq");
	}
	
	public function PAGE_terms(){
		return view('main')
				->with("page", "terms");
	}
	
	public function PAGE_privacy(){
		return view('main')
				->with("page", "privacy");
	}
	
	public function PAGE_support(){
		return view('main')
				->with("page", "support");
	}
	
		
	public function submit_support(){
		$email = Request::get('email');
		$msg = Request::get('message');
		
		$validEmail = !!filter_var($email, FILTER_VALIDATE_EMAIL);
		
		if($validEmail){
			$arr = array("email" => $email, "message" => $msg);
			\Firebase::push('/feedbacks/website/', $arr);
			return redirect()
			 	->route("support")
		        ->with("success", true);
		}
		else{
			return redirect()
			 	->route("support")
		        ->with("success", false)
		        ->withInput();
		}
		
		
	}
		
	public function CONTROL_leaderboard(){
		
		$game = Request::get('game');
		$streakEnabled = Request::get('streakEnabled');
		
		$data = \Firebase::get('/leaderboard/'.$game.'.json?orderBy="$priority"&limitToLast=15');
		$records = json_decode($data);
		
		$result = array();
		
		foreach($records as $key=>$record){
			$toGetUserId;
			if(!empty($record->leaderId)){
				$toGetUserId = $record->leaderId;
			}
			else{
				if(count($record->userIds) > 0){
					$toGetUserId = $record->userIds[0];
				}
			}
			
			if(isset($toGetUserId)){
				$profile = $this->getProfileByUserId($toGetUserId);
				$record->profile = $profile;
			}
			
			if($streakEnabled){
				$record->streakCount = $this->getStreakCountByUserId($game, $toGetUserId);
			}
			
			array_push($result, $record);
		}
		
		usort($result, function($a, $b)
		{
		    return $b->score - $a->score;
		});
		
		
		return view('controls.leaderboard')
					->with("records", $result);
	}	
	
	
	public function CONTROL_userDetails(){
		
		$userIds = explode(",", Request::get('userIdsString'));

		$users = array();
		foreach($userIds as $userId){
			if(!empty($userId)){
				array_push($users, $this->getProfileByUserId($userId));
			}
		}
		
		return view('controls.users_tooltip')
					->with("users", $users);
	}
	
	private function getStreakCountByUserId($game, $userId){
		$data = \Firebase::get('/streaks/'.$game.'/'.$userId.'/streakCount/');
		$count = json_decode($data);
		return $count;
	}
	
	private function getProfileByUserId($userId){
		$data = \Firebase::get('/users/'.$userId);
		$profile = json_decode($data);
		return $profile;
	}
	
	private function getProfilesByUserIds($userIds){
		$arr = array();
		
		foreach($userIds as $userId){
			array_push($arr, $this->getProfileByUserId($userId));
		}
		
		return $arr;
	}
	

}
