package com.mygdx.potatoandtomato.android;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.firebase.client.Firebase;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.absintflis.entrance.EntranceLoaderListener;
import com.mygdx.potatoandtomato.android.controls.MyEditText;
import com.mygdx.potatoandtomato.helpers.Analytics;
import com.mygdx.potatoandtomato.models.PushNotification;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.statics.Global;
import com.mygdx.potatoandtomato.statics.Terms;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;

public class AndroidLauncher extends AndroidApplication {

	FacebookConnector _facebookConnector;
	GCMClientManager _gcm;
	private static boolean _isVisible;
	private AndroidLauncher _this;
	private ImageLoader _imageLoader;
	private LayoutChangedFix _layoutChangedFix;
	private TextFieldFix _textFieldFix;
	private VibrateManager _vibrator;
	private View _view;
	private Broadcaster _broadcaster;
	private PTGame _ptGame;
	private AudioRecorder _audioRecorder;
	private ChartBoostHelper _chartBoostHelper;
	private InAppPurchaseHelper _inAppPurchaseHelper;
	private ShareAndRateHelper shareAndRateHelper;
	private Texts texts;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		initAnalytics();
		setBuildNumber();
		setContentView(R.layout.main_activity);
		RelativeLayout lg=(RelativeLayout)findViewById(R.id.root);
		_this = this;
		reset();

		texts = new Texts();
		_broadcaster = new Broadcaster();
		shareAndRateHelper = new ShareAndRateHelper(_broadcaster, texts, this);
		_inAppPurchaseHelper = new InAppPurchaseHelper(this, _broadcaster);
		_chartBoostHelper = new ChartBoostHelper(this, _broadcaster);
		_vibrator = new VibrateManager(_this, _broadcaster);
		_imageLoader = new ImageLoader(_this, _broadcaster);
		_facebookConnector = new FacebookConnector(this, _broadcaster);
		_gcm = new GCMClientManager(this, _broadcaster);
		_audioRecorder = new AudioRecorder(this, _broadcaster);
		Firebase.setAndroidContext(this);
		_layoutChangedFix = new LayoutChangedFix(this.getWindow().getDecorView().getRootView(), _broadcaster);

		Bundle b = getIntent().getExtras();
		String autoJoinRoomId = null;
		if(b != null){
			autoJoinRoomId = b.getString("roomId", "");
		}
		_ptGame = new PTGame(_broadcaster, autoJoinRoomId);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		_view = initializeForView(_ptGame, config);
		_textFieldFix = new TextFieldFix(this, (MyEditText) findViewById(R.id.dummyText), _view, _broadcaster);
		lg.addView(_view);

		subscribeLoadGameRequest();
		roomAliveRelated();
		subscribeOrientationChanged();

		startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
	}


	private void setBuildNumber(){
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			Global.CLIENT_VERSION =  pInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void initAnalytics(){
		MyApplication.getInstance().trackScreenView("GameScreen");
		Analytics.setTracker(MyApplication.getInstance());
	}

	private void roomAliveRelated(){
		_broadcaster.subscribe(BroadcastEvent.UPDATE_ROOM, new BroadcastListener<PushNotification>() {
			@Override
			public void onCallback(PushNotification obj, Status st) {
				GcmMessageHandler.handleNotification(_this, obj);
			}
		});

		_broadcaster.subscribe(BroadcastEvent.DESTROY_ROOM, new BroadcastListener() {
			@Override
			public void onCallback(Object obj, Status st) {
				RoomAliveHelper.dispose(_this);
			}
		});
	}


	public void subscribeOrientationChanged(){
		_broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener<Integer>() {
			@Override
			public void onCallback(final Integer obj, Status st) {
				Global.IS_POTRAIT = (obj == 0);
				Threadings.postRunnable(new Runnable() {
					@Override
					public void run() {
						if(obj == 0){		//potrait
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						}
						else{
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						}
					}
				});
			}
		});
	}

	public void subscribeLoadGameRequest(){
		_broadcaster.subscribe(BroadcastEvent.LOAD_GAME_REQUEST, new BroadcastListener<GameCoordinator>() {
			@Override
			public void onCallback(final GameCoordinator obj, Status st) {
				boolean errored = false;
				JarLoader loader = new JarLoader(_this);
				try {
					loader.load(obj, new EntranceLoaderListener() {
						@Override
						public void onLoadedSuccess() {
							_broadcaster.broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, obj, Status.SUCCESS);
						}

						@Override
						public void onLoadedFailed() {
							_broadcaster.broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, null, Status.FAILED);
						}
					});
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					_broadcaster.broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, null, Status.FAILED);
				}
			}
		});
	}

	private void reset(){
		RoomAliveHelper.dispose(_this);
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(!_inAppPurchaseHelper.onActivityResult(requestCode, resultCode, data)){
			super.onActivityResult(requestCode, resultCode, data);
			_facebookConnector.getCallbackManager().onActivityResult(requestCode,
					resultCode, data);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		RoomAliveHelper.save(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		RoomAliveHelper.restore(savedInstanceState);
	}

	@Override
	public void onBackPressed() {


		if(_chartBoostHelper != null && _chartBoostHelper.onBackPressed()){

		}
		else{
			//super.onBackPressed();
			_view.requestFocus();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		_isVisible = false;
		if(_chartBoostHelper != null) _chartBoostHelper.onPause();

		if(RoomAliveHelper.isActivated()){
			Toast.makeText(this, texts.toastPTStillRunning(), Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		_isVisible = true;

		if(_chartBoostHelper != null) _chartBoostHelper.onResume();
	}

	@Override
	protected void onDestroy() {
		_inAppPurchaseHelper.onDestroy();
		if(_ptGame != null){
			_ptGame.dispose();
		}
		stopService(new Intent(getBaseContext(), OnClearFromRecentService.class));
		reset();
		if(_chartBoostHelper != null) _chartBoostHelper.onDestroy();

		if(FlurryAgent.isSessionActive()){
			FlurryAgent.onEndSession(this);
		}

		System.exit(0);
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(_chartBoostHelper != null) _chartBoostHelper.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(_chartBoostHelper != null) _chartBoostHelper.onStop();
	}


	public static boolean isVisible() {
		return _isVisible;
	}

	public Broadcaster getBroadcaster() {
		return _broadcaster;
	}
}
