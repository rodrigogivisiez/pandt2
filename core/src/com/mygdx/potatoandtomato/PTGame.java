package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Game;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.services.*;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.models.Profile;

public class PTGame extends Game {

	Services _services;
	Textures _textures;
	Fonts _fonts;
	PTScreen _screen;
	Texts _texts;

	@Override
	public void create () {
		_services = new Services(new Textures(), new Fonts(), new Texts(),
									new Preferences(), new Profile(), new FirebaseDB(),
									new Shaders(), new Appwarp(), new Downloader());
		_screen = new PTScreen(_services);

		setScreen(_screen);

		com.mygdx.potatoandtomato.models.Game g = new com.mygdx.potatoandtomato.models.Game();
		g.setVersion("1");
		g.setAbbr("covered_chess");
		g.setGameUrl("http://www.potato-and-tomato.com/covered_chess/core.jar");
		g.setAssetUrl("http://www.potato-and-tomato.com/covered_chess/assets.zip");
		_screen.toScene(SceneEnum.PREREQUISITE, g, true);

	}



}
