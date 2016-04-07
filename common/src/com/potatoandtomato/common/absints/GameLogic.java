package com.potatoandtomato.common.absints;

import com.potatoandtomato.common.GameCoordinator;

/**
 * Created by SiongLeng on 5/4/2016.
 */
public abstract class GameLogic {

    private GameCoordinator _coordinator;

    public GameLogic(GameCoordinator gameCoordinator) {
        this._coordinator = gameCoordinator;

    }
    public GameCoordinator getCoordinator() {
        return _coordinator;
    }

    public abstract void dispose();

}
