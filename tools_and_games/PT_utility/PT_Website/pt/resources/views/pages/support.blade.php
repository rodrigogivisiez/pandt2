<div class="container support">
	
	@if(session()->has('success'))
	
	
		<div class="row">
			<div class="col-xs-12">
				@if(session('success'))
					<div class="alert alert-success" role="alert">
						We have received your message. Thanks and we will be in touch as soon as we can. 
					</div>
				@else
					<div class="alert alert-danger" role="alert">
						Fails to submit your request, please make sure your email is valid.
					</div>
				@endif
			</div>
		</div>
	@endif
	
	
	<div class="row">
		<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 supportContainerParent">
		
			<div class="supportContainer first">
				<div class="textCenter mascots">
					<img src="css/images/support_mascots.png">
				</div>
				
				<div class="content">
					We are a two person team indie game studio seeking to connect friends to build memories one game at a time. If you have any suggestion, feedback or required bug fix, please feel free to contact us via the form. Details like Phone model and Android version (e.g. Samsung S7, Android 6.0.1) will help greatly if it is bug related. Thank you so much for contacting us! We can continue because of users like you! 
				</div>
				
			</div>
			
		</div>
		
		
		<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 supportContainerParent">
			
			<div class="supportContainer">
				
				<form action="{{route('submitSupport')}}" method="post">
				  <div class="form-group">
				    <label><h4>Email</h3></label>
				    <input name="email" type="email" class="form-control" placeholder="Email" value="{{Input::old('email')}}">
				  </div>
				  <div class="form-group">
				    <label><h4>Your Message</h3></label>
				    <textarea name="message" type="text" class="form-control">{{Input::old('message')}}</textarea>
				  </div>
				  
				  <div class="textCenter">
				  	<button type="submit" class="btn btn-default">Submit</button>
				  </div>
				  
				</form>
				
				
			</div>
			
		</div>
	</div>
	
	
	
</div>