package com.potatoandtomato.games;

import com.potatoandtomato.common.MockGame;
import com.potatoandtomato.games.screens.CoveredChessScreen;

public class CoveredChessGame extends MockGame {

	public CoveredChessGame() {
		super();
	}

	@Override
	public void create() {
		super.create();
		initiateMockGamingKit(1, 0);
	}

	@Override
	public void onReady() {
		Entrance entrance = new Entrance(getCoordinator());
		setScreen(entrance.getCurrentScreen());
	}
}
