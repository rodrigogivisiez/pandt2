package com.mygdx.potatoandtomato.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.firebase.client.Firebase;
import com.mygdx.potatoandtomato.PTGame;
import com.facebook.FacebookSdk;

import java.util.Arrays;

public class AndroidLauncher extends AndroidApplication {

	FacebookConnector _facebookConnector;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_facebookConnector = new FacebookConnector(this);
		Firebase.setAndroidContext(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new PTGame(), config);
	}




}
