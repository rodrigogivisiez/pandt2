package com.potatoandtomato.games.screens;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 15/1/2016.
 */
public class MainScreenLogic {

    private GameCoordinator _coordinator;
    private Services _services;
    private MainScreen _mainScreen;

    public MainScreenLogic(Services _services, GameCoordinator _coordinator) {
        this._services = _services;
        this._coordinator = _coordinator;

        _mainScreen = new MainScreen(_coordinator, _services);
    }

    public MainScreen getMainScreen() {
        return _mainScreen;
    }
}
