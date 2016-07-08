package com.potatoandtomato.games.android;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.firebase.client.Firebase;
import com.potatoandtomato.games.PhotoHuntGame;
import com.potatoandtomato.games.statics.Global;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Global.EXPECTED_PLAYERS_DEBUG = 1;

		Firebase.setAndroidContext(this);
		initialize(new PhotoHuntGame("photo_hunt"), config);
	}
}
