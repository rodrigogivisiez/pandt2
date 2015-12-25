package com.potatoandtomato.common;

import com.badlogic.gdx.Screen;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public abstract class GameEntranceAbstract {

    GameEndListener _gameEndListener;
    GameLibCoordinator _gameLibCoordinator;

    public GameEntranceAbstract(GameLibCoordinator gameLibCoordinator) {
        _gameLibCoordinator = gameLibCoordinator;
    }

    public abstract Screen getFirstScreen();

    public GameEndListener getGameEndListener() {
        return _gameEndListener;
    }

    public void setGameEndListener(GameEndListener _gameEndListener) {
        this._gameEndListener = _gameEndListener;
    }

    public GameLibCoordinator getGameLibCoordinator() {
        return _gameLibCoordinator;
    }
}
