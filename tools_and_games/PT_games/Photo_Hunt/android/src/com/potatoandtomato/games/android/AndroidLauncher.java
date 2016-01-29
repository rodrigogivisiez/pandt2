package com.potatoandtomato.games.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.firebase.client.Firebase;
import com.potatoandtomato.games.PhotoHuntGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Firebase.setAndroidContext(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new PhotoHuntGame("photo_hunt"), config);
	}
}
