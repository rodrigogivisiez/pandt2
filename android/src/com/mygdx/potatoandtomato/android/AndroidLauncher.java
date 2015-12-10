package com.mygdx.potatoandtomato.android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.firebase.client.Firebase;
import com.mygdx.potatoandtomato.PTGame;
import com.facebook.FacebookSdk;

import java.security.NoSuchAlgorithmException;
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


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		_facebookConnector.getCallbackManager().onActivityResult(requestCode,
				resultCode, data);
	}


}
