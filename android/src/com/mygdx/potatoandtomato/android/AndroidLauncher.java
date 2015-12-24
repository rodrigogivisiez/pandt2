package com.mygdx.potatoandtomato.android;

import android.content.Context;
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
import com.potatoandtomato.common.Broadcaster;

public class AndroidLauncher extends AndroidApplication {

	FacebookConnector _facebookConnector;
	GCMClientManager _gcm;
	private View _rootView;
	private int _screenHeight;
	int width, _height;


	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		_facebookConnector.getCallbackManager().onActivityResult(requestCode,
				resultCode, data);
	}


}
