package com.potatoandtomato.games;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameEntrance;
import com.potatoandtomato.common.absints.GameScreen;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.games.assets.*;
import com.potatoandtomato.games.screens.MainScreen;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntrance {

    MyAssets assets;
    GameCoordinator coordinator;

    public Entrance(GameCoordinator gameCoordinator) {
        super(gameCoordinator);
        this.coordinator = gameCoordinator;

        initAssets();

        assets.loadAsync(new Runnable() {
            @Override
            public void run() {
                getGameCoordinator().finishLoading();
            }
        });

    }

    @Override
    public void init() {
        GameScreen mainScreen = new MainScreen(getGameCoordinator(), assets);
        getGameCoordinator().setScreen(mainScreen);
    }

    @Override
    public void onContinue() {
    }

    @Override
    public void dispose() {
        assets.dispose();
    }

    private void initAssets(){
        PTAssetsManager manager = coordinator.getPTAssetManager(true);
        Fonts fonts = new Fonts(manager);
        Patches patches = new Patches(manager);
        Sounds sounds = new Sounds(manager);
        //Textures textures = new Textures(manager, "pack.atlas");

        assets = new MyAssets(manager, fonts, null, sounds, patches, null);


    }

    public MyAssets getAssets() {
        return assets;
    }
}
