package com.potatoandtomato.games;

import com.potatoandtomato.common.mockings.MockGame;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.statics.Global;

public class CoveredChessGame extends MockGame {

	private boolean _initialized;

	public CoveredChessGame(String gameId, boolean isContinue) {
		super(gameId, isContinue);
	}

	@Override
	public void create() {
		super.create();
		initiateMockGamingKit(Global.BOT_MATCH ? 1 : 2, 1, 0, Global.DEBUG);
	}

	@Override
	public void onReady() {
		if(!_initialized){
			_initialized = true;
			final Entrance entrance = new Entrance(getCoordinator());


			Threadings.runInBackground(new Runnable() {
				@Override
				public void run() {
					while (!entrance.getAssets().getPTAssetsManager().isFinishLoading()) {
						Threadings.sleep(100);
					}

					Threadings.postRunnable(new Runnable() {
						@Override
						public void run() {
							if(!isContinue()){
								entrance.init();
							}
							else{
								entrance.onContinue();
							}
						}
					});


				}
			});
		}

	}
}
