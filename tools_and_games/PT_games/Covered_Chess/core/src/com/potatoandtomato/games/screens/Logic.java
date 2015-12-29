package com.potatoandtomato.games.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class Logic {

    Services _services;
    GameCoordinator _coordinator;
    CoveredChessScreen _screen;

    public Logic(Services services, GameCoordinator coordinator) {
        this._services = services;
        this._coordinator = coordinator;

        _screen = new CoveredChessScreen(coordinator, services);
    }

    public CoveredChessScreen getScreen() {
        return _screen;
    }
}
