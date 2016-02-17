package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.absintflis.assets.MyFreetypeFontLoader;
import com.mygdx.potatoandtomato.assets.*;
import com.mygdx.potatoandtomato.assets.Sounds;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class Assets {

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
}
