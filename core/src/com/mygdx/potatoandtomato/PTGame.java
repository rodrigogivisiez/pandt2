package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Game;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.controls.Chat;
import com.mygdx.potatoandtomato.helpers.services.*;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.models.Profile;

import java.util.ArrayList;

public class PTGame extends Game {

	Services _services;
	Textures _textures;
	Fonts _fonts;
	PTScreen _screen;
	Texts _texts;

	@Override
	public void create () {
		_services = new Services(new Textures(), new Fonts(), new Texts(),
									new Preferences(), new Profile(), new FirebaseDB(Terms.FIREBASE_URL),
									new Shaders(), new Appwarp(), new Downloader(), new Chat());
		_screen = new PTScreen(_services);

		setScreen(_screen);

		_screen.toScene(SceneEnum.BOOT);



	}



}
