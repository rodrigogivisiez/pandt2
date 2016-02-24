package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.absint.IAssetFragment;
import com.potatoandtomato.games.helpers.SoundsWrapper;

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

    public Sounds(AssetManager _manager, GameCoordinator coordinator) {
        this._manager = _manager;
        _soundsDirectory = coordinator.getFileH("sounds");
        _soundsMap = new HashMap<String, Sound>();
        _musicsMap = new HashMap<String, Music>();
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
        START_GAME, OPEN_SLIDE,
        FLIP_CHESS, MOVE_CHESS,
        FIGHT_CHESS, WIN, LOSE,
        THEME
    }

}
