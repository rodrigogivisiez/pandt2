package com.potatoandtomato.games.abs.screens;

import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.GameScreen;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.MainController;
import com.potatoandtomato.games.models.Service;

/**
 * Created by SiongLeng on 2/2/2016.
 */
public abstract class LogicAbstract implements Disposable {

    private GameCoordinator _gameCoordinator;
    private MainController _mainController;
    private Assets _assets;
    private Service _service;

    public LogicAbstract(MainController mainController){
        _mainController = mainController;
        _gameCoordinator = mainController.getCoordinator();
        _service = mainController.getService();
        _assets = _service.getAssets();
    }

    public abstract GameScreen getScreen();

    protected Assets getAssets() {
        return _assets;
    }

    protected MainController getMainController() {
        return _mainController;
    }

    protected GameCoordinator getGameCoordinator() {
        return _gameCoordinator;
    }

    protected Service getService() {
        return _service;
    }

    @Override
    public void dispose() {
        getScreen().dispose();
    }
}
