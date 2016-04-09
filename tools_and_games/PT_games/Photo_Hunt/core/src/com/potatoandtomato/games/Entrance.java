package com.potatoandtomato.games;

import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameEntrance;
import com.potatoandtomato.games.assets.*;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.screens.main.MainLogic;
import com.potatoandtomato.games.services.Database;
import com.potatoandtomato.games.services.RoomMsgHandler;
import com.potatoandtomato.games.services.SoundsWrapper;
import com.potatoandtomato.games.services.Texts;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntrance {

    MyAssets _assets;
    Services _services;
    GameCoordinator _coordinator;
    MainLogic _logic;


    public Entrance(GameCoordinator gameCoordinator) {
        super(gameCoordinator);
        this._coordinator = gameCoordinator;
        getGameCoordinator().setLandscape();

        initAssets();

        _assets.loadBasic(new Runnable() {
            @Override
            public void run() {

                _logic = new MainLogic(getServices(), getGameCoordinator());

                getGameCoordinator().finishLoading();
            }
        });

    }

    @Override
    public void init() {
        _logic.init();
        getGameCoordinator().getGame().setScreen((_logic.getMainScreen()));
    }

    @Override
    public void onContinue() {
//        getGameCoordinator().getGame().setScreen((_logic.getScreen()));
//        _logic.continueGame();
    }

    @Override
    public void dispose() {
//        _services.getSoundsWrapper().dispose();
//        _services.getScoresHandler().dispose();
//        _services.getAssets().dispose();
        if(_logic != null) _logic.dispose();
    }

    private void initAssets(){
        AssetManager manager = _coordinator.getAssetManager(true);
        Fonts fonts = new Fonts(manager);
        Patches patches = new Patches();
        Sounds sounds = new Sounds(manager);
        Textures textures = new Textures(manager, "pack.atlas");

        _assets = new MyAssets(manager, fonts, null, sounds, patches, textures);
    }

    public Services getServices() {
        if(_services == null){
            Database database = new Database(_coordinator.getFirebase());
            Texts texts = new Texts();
            _services = new Services(_assets, new SoundsWrapper(_assets, _coordinator), database,
                    texts, new RoomMsgHandler(_coordinator));
        }
        return _services;
    }

    public void setServices(Services _services) {
        this._services = _services;
    }
}
