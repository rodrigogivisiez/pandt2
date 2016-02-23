package com.potatoandtomato.games;

import com.potatoandtomato.common.MockGame;
import com.potatoandtomato.games.statics.Global;

public class CoveredChessGame extends MockGame {

	private boolean _initialized;

	public boolean isContinue;

	public CoveredChessGame(String gameId) {
		super(gameId);
	}

	@Override
	public void create() {
		super.create();
		initiateMockGamingKit(Global.DEBUG ? 0 : 2, Global.DEBUG ? 0 : 1);
	}

	@Override
	public void onReady() {
		if(!_initialized){
			_initialized = true;
			Entrance entrance = new Entrance(getCoordinator());

			if(!isContinue){
				entrance.init();
			}
			else{
				entrance.onContinue();
			}
		}

	}
}
