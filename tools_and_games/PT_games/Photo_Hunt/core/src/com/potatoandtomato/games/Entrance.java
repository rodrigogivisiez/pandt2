package com.potatoandtomato.games;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.GameEntrance;
import com.potatoandtomato.games.abs.database.IDatabase;
import com.potatoandtomato.games.assets.Assets;
import com.potatoandtomato.games.helpers.Database;
import com.potatoandtomato.games.helpers.ImageGetter;
import com.potatoandtomato.games.helpers.MainController;
import com.potatoandtomato.games.models.Service;
import com.potatoandtomato.games.screens.loading_screen.LoadingLogic;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntrance {

    MainController _controller;
    Assets _assets;
    IDatabase _database;
    Service _service;

    public Entrance(GameCoordinator gameCoordinator) {
        super(gameCoordinator);
        getGameCoordinator().setLandscape();

        _assets = new Assets(gameCoordinator);
        _assets.load();

        _database = new Database(gameCoordinator.getFirebase());
        _service = new Service(_database, _assets, new ImageGetter(gameCoordinator, _database));
        _service.getImageGetter().init();
        _controller = new MainController(gameCoordinator, _service);

        gameCoordinator.finishLoading();

    }

    @Override
    public void init() {
        _controller.init();
    }

    @Override
    public void onContinue() {
       // getGameCoordinator().getGame().setScreen(_logic.getScreen());
    }

    @Override
    public void dispose() {
        _assets.dispose();
        _service.getImageGetter().dispose();
        _controller.dispose();

    }

}
