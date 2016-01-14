package com.potatoandtomato.games;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.IPTGame;
import com.potatoandtomato.common.MockGame;

public class ChineseChessGame extends MockGame {

	public ChineseChessGame(String gameId) {
		super(gameId);
	}

	@Override
	public void create() {
		super.create();
		initiateMockGamingKit(1, 0);
	}

	@Override
	public void onReady() {
		Entrance entrance = new Entrance(getCoordinator());
		entrance.init();
	}
}
