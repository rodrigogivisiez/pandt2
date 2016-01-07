package com.potatoandtomato.common;

import com.badlogic.gdx.Screen;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public abstract class GameScreen implements Screen{

    private GameCoordinator _coordinator;

    public GameScreen(GameCoordinator gameCoordinator) {
        this._coordinator = gameCoordinator;

    }
    public GameCoordinator getCoordinator() {
        return _coordinator;
    }

}
