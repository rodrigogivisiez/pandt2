package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.potatoandtomato.absintflis.assets.IAssetFragment;
import com.potatoandtomato.common.GameCoordinator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Sounds implements IAssetFragment {

    private AssetManager _manager;

    FileHandle _soundsDirectory;
    private HashMap<String, Sound> _soundsMap;
    private HashMap<String, Music> _musicsMap;

    public Sounds(AssetManager _manager) {
        this._manager = _manager;
        _soundsMap = new HashMap<String, Sound>();
        _musicsMap = new HashMap<String, Music>();
        _soundsDirectory = Gdx.files.internal("sounds");
    }

    @Override
    public void load() {

        for(FileHandle soundFile : _soundsDirectory.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.contains(".ogg") || s.contains(".mp3");
            }
        })){
            if(soundFile.name().contains(".mp3")){
                _manager.load("sounds/" + soundFile.name(), Music.class);
            }
            else{
                _manager.load("sounds/" + soundFile.name(), Sound.class);
            }

        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onLoaded() {

        for(FileHandle soundFile : _soundsDirectory.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.contains(".ogg") || s.contains(".mp3");
            }
        })){
            if(soundFile.name().contains(".mp3")){
                _musicsMap.put(soundFile.nameWithoutExtension(), _manager.get("sounds/" + soundFile.name(), Music.class));
            }
            else{
                _soundsMap.put(soundFile.nameWithoutExtension(), _manager.get("sounds/" + soundFile.name(), Sound.class));
            }

        }
    }

    public Sound getSound(Name name){
        return _soundsMap.get(name.name());
    }

    public Music getMusic(Name name) { return _musicsMap.get(name.name()); }

    public enum Name{
        BUTTON_CLICKED, TOGETHER_CHEERS,
        SLIDING, GAME_CREATED, MIC, COUNT_DOWN, MESSAGING,
        THEME,
        TOGETHER_HAPPY, TOGETHER_BORED, TOGETHER_FAILED, TOGETHER_CRY, TOGETHER_ANTICIPATING,
        MOVING_RANK_END, STREAK_DIED, ADDING_SCORE, MOVING_RANK, STREAK, SCORE_APPEAR
    }
}
