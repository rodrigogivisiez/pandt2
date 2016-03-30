package com.potatoandtomato.games;

import com.badlogic.gdx.assets.AssetManager;
import com.firebase.client.Firebase;
import com.potatoandtomato.common.GameEntrance;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.games.assets.*;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.screens.BoardLogic;
import com.potatoandtomato.games.services.*;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntrance {

    BoardLogic _logic;
    Services _services;
    MyAssets _assets;
    GameCoordinator _coordinator;

    public Entrance(GameCoordinator gameCoordinator) {
        super(gameCoordinator);
        this._coordinator = gameCoordinator;

        initAssets();

        _assets.loadBasic(new Runnable() {
            @Override
            public void run() {
                Database database = new Database(_coordinator);
                Texts texts = new Texts();
                GameDataController gameDataController = new GameDataController(_coordinator);

                _services =  new Services(_assets, texts, new SoundsWrapper(_assets, _coordinator),
                        database, new ScoresHandler(_coordinator, database, texts, gameDataController), gameDataController);

                _logic = new BoardLogic(_services, getGameCoordinator());

                getGameCoordinator().finishLoading();
            }
        });

    }

    @Override
    public void init() {
        _logic.init();
        getGameCoordinator().getGame().setScreen((_logic.getScreen()));
        Firebase db = getGameCoordinator().getFirebase();
    }

    @Override
    public void onContinue() {
        getGameCoordinator().getGame().setScreen((_logic.getScreen()));
        _logic.continueGame();
    }

    @Override
    public void dispose() {
        _services.getSoundsWrapper().dispose();
        _services.getScoresHandler().dispose();
        _services.getAssets().dispose();
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


}
