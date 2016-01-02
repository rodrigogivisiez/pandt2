package com.potatoandtomato.games;

import com.potatoandtomato.common.MockGame;

public class CoveredChessGame extends MockGame {

	public CoveredChessGame(boolean isHost) {
		super(isHost);
	}

	@Override
	public void create() {
		super.create();
		initiateMockGamingKit(2, 1);
	}

	@Override
	public void onReady() {
		Entrance entrance = new Entrance(getCoordinator());
		setScreen(entrance.getCurrentScreen());
		entrance.init();
	}
}
