package com.potatoandtomato.games;

import com.badlogic.gdx.Screen;
import com.potatoandtomato.common.GameEntrance;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.GameScreen;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntrance {

    public Entrance(GameCoordinator gameCoordinator) {
        super(gameCoordinator);
        setCurrentScreen(new SampleScreen(gameCoordinator));
    }

}
