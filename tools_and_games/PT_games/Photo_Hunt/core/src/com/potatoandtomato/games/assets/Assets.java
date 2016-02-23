package com.potatoandtomato.games.assets;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.Threadings;

import java.util.HashMap;

/**
 * Created by SiongLeng on 21/1/2016.
 */
public class Assets {

    private AssetManager _manager;
    private Fonts _fonts;
    private Textures _textures;


    private GameCoordinator _coordinator;
    public Assets(GameCoordinator gameCoordinator) {
        _coordinator = gameCoordinator;
        _manager = gameCoordinator.getAssetManager();
        _fonts = new Fonts(_manager, _coordinator);

    }

    public void load(){
        _manager.load(_texturePackPath, TextureAtlas.class);

        _fonts.preLoad();
        _manager.finishLoading();

        loadTextures();
        _fonts.finishLoading();
    }

    public Fonts getFonts() {
        return _fonts;
    }

    private TextureAtlas _texturePack;
    private String _texturePackPath = "photohunt_pack.atlas";
    private TextureRegion sampleOne, sampleTwo, empty, circle, loading, whiteRound;

    public void loadTextures(){
        _texturePack = _manager.get(_texturePackPath, TextureAtlas.class);
        sampleOne = _texturePack.findRegion("1");
        sampleTwo = _texturePack.findRegion("2");
        empty = _texturePack.findRegion("empty");
        circle = _texturePack.findRegion("circle");
        loading = _texturePack.findRegion("loading");
        whiteRound = _texturePack.findRegion("btn_white_round");
    }

    public TextureRegion getWhiteRound() {
        return whiteRound;
    }

    public TextureRegion getLoading() {
        return loading;
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
