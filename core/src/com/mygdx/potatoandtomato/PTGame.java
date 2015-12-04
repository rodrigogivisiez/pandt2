package com.mygdx.potatoandtomato;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.mygdx.potatoandtomato.abstractions.LogicAbstract;
import com.mygdx.potatoandtomato.enums.ScreenEnum;
import com.mygdx.potatoandtomato.helpers.assets.Textures;
import com.mygdx.potatoandtomato.screens.BootLogic;

public class PTGame extends Game {

	Textures _textures;

	@Override
	public void create () {
		_textures = new Textures();
		switchScreen(ScreenEnum.BOOT);
	}

	public void switchScreen(ScreenEnum screenEnum){

		LogicAbstract logic = null;
		switch (screenEnum){

			case BOOT:
				logic = new BootLogic(this, _textures);
				break;

		}

		if(logic.getScreen() != null){
			this.setScreen(logic.getScreen());
		}
		else{
			throw new NullPointerException();
		}



	}



}
