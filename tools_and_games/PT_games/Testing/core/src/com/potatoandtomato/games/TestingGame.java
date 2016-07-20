package com.potatoandtomato.games;

import com.potatoandtomato.common.mockings.MockGame;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.statics.Global;

public class TestingGame extends MockGame {

	private boolean _initialized;

	public boolean isContinue;

	public TestingGame(String gameId) {
		super(gameId, false);
	}

	@Override
	public void create() {
		super.create();
		initiateMockGamingKit(1, 1, 0, Global.DEBUG);
	}

	@Override
	public void onReady() {
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
						if (!isContinue()) {
							entrance.init();
						} else {
							entrance.onContinue();
						}
					}
				});
			}
		});

	}
}
