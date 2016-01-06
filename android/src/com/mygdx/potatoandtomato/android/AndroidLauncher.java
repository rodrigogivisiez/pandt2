package com.mygdx.potatoandtomato.android;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import android.support.annotation.Keep;
import android.view.View;
import android.view.WindowManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.firebase.client.Firebase;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.SafeThread;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import com.potatoandtomato.common.GameCoordinator;

public class AndroidLauncher extends AndroidApplication {

	FacebookConnector _facebookConnector;
	GCMClientManager _gcm;
	private static boolean _isVisible;
	private AndroidLauncher _this;
	private ImageLoader _imageLoader;
	private KeepAlive _keepAlive;
	private LayoutChangedFix _layoutChangedFix;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_this = this;
		_imageLoader = new ImageLoader(_this);
		_keepAlive = new KeepAlive(_this);
		_facebookConnector = new FacebookConnector(this);
		_gcm = new GCMClientManager(this);
		Firebase.setAndroidContext(this);
		_layoutChangedFix = new LayoutChangedFix(this.getWindow().getDecorView().getRootView());

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new PTGame(), config);

		Broadcaster.getInstance().subscribe(BroadcastEvent.DESTROY_ROOM, new BroadcastListener() {
			@Override
			public void onCallback(Object obj, Status st) {
				GcmMessageHandler.destroyRoom(_this);
			}
		});

		subscribeLoadGameRequest();


	}

	public void subscribeLoadGameRequest(){
		Broadcaster.getInstance().subscribe(BroadcastEvent.LOAD_GAME_REQUEST, new BroadcastListener<GameCoordinator>() {
			@Override
			public void onCallback(GameCoordinator obj, Status st) {
				JarLoader loader = new JarLoader(_this);
				try {
					obj = loader.load(obj);
					Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, obj, Status.SUCCESS);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, null, Status.FAILED);
				}

			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		_facebookConnector.getCallbackManager().onActivityResult(requestCode,
				resultCode, data);
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
		GcmMessageHandler.destroyRoom(_this);
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



}
