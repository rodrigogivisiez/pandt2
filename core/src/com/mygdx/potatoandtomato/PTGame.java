package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.assets.Fonts;
import com.mygdx.potatoandtomato.helpers.assets.Textures;

public class PTGame extends Game {

	Textures _textures;
	Fonts _fonts;
	PTScreen _screen;

	@Override
	public void create () {
		_textures = new Textures();
		_fonts = new Fonts();
		_screen = new PTScreen(_textures, _fonts);

		setScreen(_screen);
		_screen.toScene(SceneEnum.BOOT);

	}



}
