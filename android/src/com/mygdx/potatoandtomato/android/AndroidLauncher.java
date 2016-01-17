package com.mygdx.potatoandtomato.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.firebase.client.Firebase;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.models.NativeLibgdxTextInfo;
import com.potatoandtomato.common.*;

public class AndroidLauncher extends AndroidApplication {

	FacebookConnector _facebookConnector;
	GCMClientManager _gcm;
	private static boolean _isVisible;
	private AndroidLauncher _this;
	private ImageLoader _imageLoader;
	private KeepAlive _keepAlive;
	private LayoutChangedFix _layoutChangedFix;
	private TextFieldFix _textFieldFix;
	private View _view;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);
		RelativeLayout lg=(RelativeLayout)findViewById(R.id.root);
		_this = this;
		reset();

		_imageLoader = new ImageLoader(_this);
		_keepAlive = new KeepAlive(_this);
		_facebookConnector = new FacebookConnector(this);
		_gcm = new GCMClientManager(this);
		Firebase.setAndroidContext(this);
		_layoutChangedFix = new LayoutChangedFix(this.getWindow().getDecorView().getRootView());


		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		_view = initializeForView(new PTGame(), config);
		_textFieldFix = new TextFieldFix(this, (EditText) findViewById(R.id.dummyText), _view);
		lg.addView(_view);

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

	private void reset(){
		Broadcaster.getInstance().dispose();
		GcmMessageHandler.destroyRoom(_this);
		if(_keepAlive != null) _keepAlive.release();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		_facebookConnector.getCallbackManager().onActivityResult(requestCode,
				resultCode, data);
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



}
