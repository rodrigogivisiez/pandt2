package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.assets.*;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class Assets implements Disposable {

    AssetManager _manager;
    Fonts _fonts;
    Patches _patches;
    Textures _textures;
    Sounds _sounds;
    Animations _animations;

    public Assets() {
        _manager = new AssetManager();
        _fonts = new Fonts(_manager);
        _textures = new Textures(_manager);
        _patches = new Patches();
        _sounds = new Sounds(_manager);
        _animations = new Animations();
    }

    public void loadBasic(Runnable onFinish){

        _fonts.load();
        _textures.load();
        _sounds.load();
        _animations.load();

        _manager.finishLoading();

        _fonts.onLoaded();
        _textures.onLoaded();
        _patches.onLoaded(_textures.getUIPack());
        _sounds.onLoaded();
        _animations.onLoaded();

        if(onFinish != null) onFinish.run();
    }


    public Fonts getFonts() {
        return _fonts;
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

    public Animations getAnimations() {
        return _animations;
    }

    @Override
    public void dispose() {
        _manager.dispose();
    }
}
