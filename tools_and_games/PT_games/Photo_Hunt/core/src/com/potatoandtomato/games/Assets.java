package com.potatoandtomato.games;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.potatoandtomato.common.GameCoordinator;

import java.util.HashMap;

/**
 * Created by SiongLeng on 21/1/2016.
 */
public class Assets {

    private AssetManager _manager;
    private GameCoordinator _coordinator;
    private HashMap<Integer, Music> _musicHashMap;
    private HashMap<Integer, Texture> _textureHashMap;

    public Assets(GameCoordinator gameCoordinator) {

        _coordinator = gameCoordinator;
        _manager = gameCoordinator.getAssetManager();


        _musicHashMap = new HashMap<Integer, Music>();
        _textureHashMap = new HashMap<Integer, Texture>();

    }

    public void load(){
//        for(int i = 1; i < 15; i++){
//            _manager.load(i + ".mp3", Music.class);
//            _manager.load(i + ".jpg", Texture.class);
//        }
//
//        _manager.finishLoading();
//
//        populate();
    }

    public void populate(){
        for(int i = 1; i < 15; i++){
            _musicHashMap.put(i, _manager.get(i + ".mp3", Music.class));
            _textureHashMap.put(i, _manager.get(i + ".jpg", Texture.class));
        }

        _manager.finishLoading();
    }


    public Music getMusic(int i){
        return _musicHashMap.get(i);
    }

    public Texture getTexture(int i){
        return _textureHashMap.get(i);
    }

    public void dispose(){
        for(Music music : _musicHashMap.values()){
            _coordinator.getSoundManager().disposeMusic(music);
        }
    }

}
