package com.potatoandtomato.common.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.potatoandtomato.common.absints.IAssetFragment;
import com.potatoandtomato.common.absints.PTAssetsManager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

/**
 * Created by SiongLeng on 29/3/2016.
 */
public abstract class SoundAssets implements IAssetFragment {

    private PTAssetsManager _manager;

    FileHandle _soundsDirectory;
    private HashMap<String, Sound> _soundsMap;
    private HashMap<String, Music> _musicsMap;

    public SoundAssets(PTAssetsManager _manager) {
        this._manager = _manager;
        _soundsMap = new HashMap<String, Sound>();
        _musicsMap = new HashMap<String, Music>();
        _soundsDirectory =  _manager.getFileHandleResolver().resolve("sounds");
    }

    @Override
    public void load() {

        for(FileHandle soundFile : _soundsDirectory.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.toLowerCase().contains(".ogg") || s.toLowerCase().contains(".mp3");
            }
        })){
            if(soundFile.name().toLowerCase().contains(".mp3") || soundFile.name().toLowerCase().contains("_music")){
                _manager.load("sounds/" + soundFile.name(), Music.class);
            }
            else{
                _manager.load("sounds/" + soundFile.name(), Sound.class);
            }

        }
    }

    @Override
    public void dispose() {
        _soundsMap.clear();
        _musicsMap.clear();
    }

    @Override
    public void onLoaded() {

        for(FileHandle soundFile : _soundsDirectory.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.toLowerCase().contains(".ogg") || s.toLowerCase().contains(".mp3");
            }
        })){
            if(soundFile.name().toLowerCase().contains(".mp3") || soundFile.name().toLowerCase().contains("_music")){
                _musicsMap.put(soundFile.nameWithoutExtension(), _manager.get("sounds/" + soundFile.name(), Music.class));
            }
            else{
                _soundsMap.put(soundFile.nameWithoutExtension(), _manager.get("sounds/" + soundFile.name(), Sound.class));
            }

        }
    }

    public Sound getSound(Object object){
        return _soundsMap.get(object.toString());
    }

    public Music getMusic(Object object) {
        return _musicsMap.get(object.toString());
    }




}
