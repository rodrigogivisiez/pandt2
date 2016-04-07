package com.potatoandtomato.games;

import com.potatoandtomato.common.mockings.MockGame;
import com.potatoandtomato.games.statics.Global;

public class PhotoHuntGame extends MockGame {

	private boolean _initialized;

	public boolean isContinue;

	public PhotoHuntGame(String gameId) {
		super(gameId);
	}

	@Override
	public void create() {
		super.create();
		initiateMockGamingKit(1, 2, Global.DEBUG);
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
