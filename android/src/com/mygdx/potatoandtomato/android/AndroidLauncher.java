package com.mygdx.potatoandtomato.android;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import android.view.View;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.firebase.client.Firebase;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import com.potatoandtomato.common.GameCoordinator;

public class AndroidLauncher extends AndroidApplication {

	FacebookConnector _facebookConnector;
	GCMClientManager _gcm;
	private View _rootView;
	private int _screenHeight;
	int width, _height;
	private static boolean _isVisible;
	private AndroidLauncher _this;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_this = this;
		_facebookConnector = new FacebookConnector(this);
		_gcm = new GCMClientManager(this);
		Firebase.setAndroidContext(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		_rootView = this.getWindow().getDecorView().getRootView();
		Rect rect = new Rect();
		_rootView.getWindowVisibleDisplayFrame(rect);
		_screenHeight = rect.height();
		addLayoutChangedListener();

		initialize(new PTGame(), config);

		Broadcaster.getInstance().subscribe(BroadcastEvent.DESTROY_ROOM, new BroadcastListener() {
			@Override
			public void onCallback(Object obj, Status st) {
				GcmMessageHandler.destroyRoom(_this);
			}
		});

		subscribeLoadGameRequest();
	}


	private void addLayoutChangedListener(){
		_rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int left, int top, int right,
									   int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

				Rect rect = new Rect();
				_rootView.getWindowVisibleDisplayFrame(rect);

				if (!(width == rect.width() && _height == rect.height())) {
					width = rect.width();
					_height = rect.height();
					Broadcaster.getInstance().broadcast(BroadcastEvent.SCREEN_LAYOUT_CHANGED,
							Positions.screenYToGdxY(_screenHeight - _height, _screenHeight));

				}
			}
		});
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

	public static boolean isVisible() {
		return _isVisible;
	}



}
