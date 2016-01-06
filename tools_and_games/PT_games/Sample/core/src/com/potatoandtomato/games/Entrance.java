package com.potatoandtomato.games;

import com.badlogic.gdx.Screen;
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
        setCurrentScreen(_screen);
    }

    @Override
    public void init() {

    }

    @Override
    public void dispose() {
        _screen.dispose();
    }

}
