package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Game;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.assets.*;
import com.mygdx.potatoandtomato.helpers.utils.Assets;

public class PTGame extends Game {

	Assets _assets;
	Textures _textures;
	Fonts _fonts;
	PTScreen _screen;
	Texts _texts;

	@Override
	public void create () {
		_assets = new Assets(new Textures(), new Fonts(), new Texts(),
									new Preferences(), new Profile(), new FirebaseDB(), new Shaders());
		_screen = new PTScreen(_assets);

		setScreen(_screen);
		_screen.toScene(SceneEnum.BOOT);

	}



}
