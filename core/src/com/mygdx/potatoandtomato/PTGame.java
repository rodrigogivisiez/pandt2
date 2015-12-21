package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.controls.Chat;
import com.mygdx.potatoandtomato.helpers.services.*;
import com.mygdx.potatoandtomato.helpers.utils.Logs;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.models.Profile;

public class PTGame extends Game {

	Services _services;
	Assets _assets;
	PTScreen _screen;
	Texts _texts;

	@Override
	public void create () {

		_assets = new Assets();
		Preferences preferences = new Preferences();
		_services = new Services(_assets, new Texts(),
									preferences, new Profile(), new FirebaseDB(Terms.FIREBASE_URL),
									new Shaders(), new Appwarp(), new Downloader(), new Chat(), new Socials(preferences));
		_screen = new PTScreen(_services);

		//run when assets done loading
		_assets.loadBasic(new Runnable() {
			@Override
			public void run() {
				setScreen(_screen);
				_screen.toScene(SceneEnum.BOOT);
			}
		});


		Logs.startLogFps();


	}



}
