package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.potatoandtomato.common.GameCoordinator;

import java.util.HashMap;

/**
 * Created by SiongLeng on 21/1/2016.
 */
public class Assets {

    private AssetManager _manager;
    private GameCoordinator _coordinator;
    public Assets(GameCoordinator gameCoordinator) {
        _coordinator = gameCoordinator;
        _manager = gameCoordinator.getAssetManager();

    }

    public void load(){

        _manager.load(_texturePackPath, TextureAtlas.class);
        _manager.finishLoading();

        loadTextures();
    }

    private TextureAtlas _texturePack;
    private String _texturePackPath = "photohunt_pack.atlas";
    private TextureRegion sampleOne, sampleTwo, empty, circle;

    public void loadTextures(){
        _texturePack = _manager.get(_texturePackPath, TextureAtlas.class);
        sampleOne = _texturePack.findRegion("1");
        sampleTwo = _texturePack.findRegion("2");
        empty = _texturePack.findRegion("empty");
        circle = _texturePack.findRegion("circle");
    }

    public TextureRegion getCircle() {
        return circle;
    }

    public TextureRegion getEmpty() {
        return empty;
    }

    public TextureRegion getSampleTwo() {
        return sampleTwo;
    }

    public TextureRegion getSampleOne() {
        return sampleOne;
    }

    public void dispose(){

    }

}
