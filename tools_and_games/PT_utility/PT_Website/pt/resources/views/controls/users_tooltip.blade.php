
@foreach($users as $user)
<div class="userTooltip">
	<img src="{{getCountryFlagPath(isset($user->country) ? $user->country : '')}}">
	{{$user->gameName}}
</div>
@endforeach