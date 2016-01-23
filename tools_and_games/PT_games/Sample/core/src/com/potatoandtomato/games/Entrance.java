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
    Assets _assets;

    public Entrance(GameCoordinator gameCoordinator) {
        super(gameCoordinator);

        _assets = new Assets(gameCoordinator);
        _assets.load();

        _screen = new SampleScreen(gameCoordinator, _assets);

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
        _assets.dispose();
        _screen.dispose();
    }

}
