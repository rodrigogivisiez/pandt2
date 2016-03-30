package com.potatoandtomato.games;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.GameEntrance;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.GameScreen;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntrance {

    SampleScreen _screen;

    public Entrance(GameCoordinator gameCoordinator) {
        super(gameCoordinator);

        _screen = new SampleScreen(gameCoordinator);

    }

    @Override
    public void init() {
        getGameCoordinator().getGame().setScreen(_screen);
    }

    @Override
    public void onContinue() {
        getGameCoordinator().getGame().setScreen(_screen);
    }

    @Override
    public void dispose() {
        _screen.dispose();
    }

}
