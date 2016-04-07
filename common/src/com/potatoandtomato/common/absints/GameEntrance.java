package com.potatoandtomato.common.absints;

import com.potatoandtomato.common.GameCoordinator;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public abstract class GameEntrance {

    private GameCoordinator _gameCoordinator;
    private GameScreen _currentScreen;

    public GameEntrance(GameCoordinator gameCoordinator) {
        _gameCoordinator = gameCoordinator;
    }

    public GameCoordinator getGameCoordinator() {
        return _gameCoordinator;
    }

    public abstract void init();

    public abstract void dispose();

    public abstract void onContinue();

}
