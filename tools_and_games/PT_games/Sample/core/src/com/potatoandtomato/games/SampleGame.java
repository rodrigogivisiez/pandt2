package com.potatoandtomato.games;

import com.badlogic.gdx.Game;

public class SampleGame extends Game {

	public SampleGame() {

	}

	@Override
	public void create () {
		SampleScreen test = new SampleScreen();
		setScreen(test);
	}


}
