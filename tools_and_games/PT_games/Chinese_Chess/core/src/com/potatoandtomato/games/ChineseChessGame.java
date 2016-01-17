package com.potatoandtomato.games;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.IPTGame;
import com.potatoandtomato.common.MockGame;

public class ChineseChessGame extends MockGame {

	public boolean isContinue;

	public ChineseChessGame(String gameId) {
		super(gameId);
	}

	@Override
	public void create() {
		super.create();
		initiateMockGamingKit(2, 1);
	}

	@Override
	public void onReady() {
		Entrance entrance = new Entrance(getCoordinator());

		if(!isContinue){
			entrance.init();
		}
		else{
			entrance.onContinue();
		}


	}
}
