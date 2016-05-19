package com.mygdx.potatoandtomato.android;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.firebase.client.Firebase;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.absintflis.entrance.EntranceLoaderListener;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;

import java.io.File;
import java.io.IOException;

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

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setBuildNumber();
		setContentView(R.layout.main_activity);
		RelativeLayout lg=(RelativeLayout)findViewById(R.id.root);
		_this = this;
		reset();

		_broadcaster = new Broadcaster();
		_vibrator = new VibrateManager(_this, _broadcaster);
		_imageLoader = new ImageLoader(_this, _broadcaster);
		_facebookConnector = new FacebookConnector(this, _broadcaster);
		_gcm = new GCMClientManager(this, _broadcaster);
		_audioRecorder = new AudioRecorder(_broadcaster);
		Firebase.setAndroidContext(this);
		_layoutChangedFix = new LayoutChangedFix(this.getWindow().getDecorView().getRootView(), _broadcaster);
		_ptGame = new PTGame(_broadcaster);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		_view = initializeForView(_ptGame, config);
		_textFieldFix = new TextFieldFix(this, (EditText) findViewById(R.id.dummyText), _view, _broadcaster);
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

	private void roomAliveRelated(){
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
			public void onCallback(Integer obj, Status st) {
				Global.IS_POTRAIT = (obj == 0);
				if(obj == 0){		//potrait
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
				else{
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
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
		super.onActivityResult(requestCode, resultCode, data);
		_facebookConnector.getCallbackManager().onActivityResult(requestCode,
				resultCode, data);
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
		_view.requestFocus();
	}

	@Override
	protected void onPause() {
		super.onPause();
		_isVisible = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		_isVisible = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(new Intent(getBaseContext(), OnClearFromRecentService.class));
		reset();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}



	public static boolean isVisible() {
		return _isVisible;
	}

	public Broadcaster getBroadcaster() {
		return _broadcaster;
	}
}
