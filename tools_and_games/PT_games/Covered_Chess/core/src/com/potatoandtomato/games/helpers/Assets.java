package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.assets.*;
import com.potatoandtomato.games.assets.Sounds;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class Assets {

    private GameCoordinator _coordinator;
    private AssetManager _manager;
    private Fonts _fonts;
    private Sounds _sounds;
    private Patches _patches;
    private Textures _textures;

    public Assets(GameCoordinator coordinator) {
        _coordinator = coordinator;
        _manager = coordinator.getAssetManager();

        _fonts = new Fonts(_manager, _coordinator);
        _sounds = new Sounds(_manager);
        _textures = new Textures(_manager);
        _patches = new Patches();

    }

    public void loadAll(Runnable onFinish){

        _textures.load();
        _fonts.load();
        _sounds.load();

        _manager.finishLoading();

        _textures.onLoaded();
        _patches.setPack(_textures.getPack());
        _patches.onLoaded();
        _sounds.onLoaded();
        _fonts.onLoaded();

        if(onFinish != null) onFinish.run();

    }


    public Textures getTextures() {
        return _textures;
    }

    public Patches getPatches() {
        return _patches;
    }

    public Sounds getSounds() {
        return _sounds;
    }

    public Fonts getFonts() {
        return _fonts;
    }
}
