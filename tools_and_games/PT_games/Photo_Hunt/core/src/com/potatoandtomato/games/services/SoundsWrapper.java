package com.potatoandtomato.games.services;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.assets.SoundAssets;
import com.potatoandtomato.games.assets.Sounds;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class SoundsWrapper implements Disposable {

    private Assets _assets;
    private GameCoordinator _coordinator;
    private ArrayList<Sound> _loopingSounds;

    public SoundsWrapper(Assets _assets, GameCoordinator coordinator) {
        this._assets = _assets;
        this._coordinator = coordinator;
        this._loopingSounds = new ArrayList();

//        _themeMusic = _assets.getSounds().getMusic(Sounds.Name.THEME_MUSIC);
//        _themeMusic.setLooping(true);
//
//        _themeSuddenDMusic = _assets.getSounds().getMusic(Sounds.Name.THEME_SUDDEN_D_MUSIC);
//        _themeSuddenDMusic.setLooping(true);
//
//        _coordinator.getSoundsPlayer().addMusic(_themeMusic);
//        _coordinator.getSoundsPlayer().addMusic(_themeSuddenDMusic);
    }

    public void playMusic(Sounds.Name name){
        Music music = _assets.getSounds().getMusic(name);
        _coordinator.getSoundsPlayer().playMusic(music);
    }

    public void playMusicNoLoop(Sounds.Name name){
        Music music = _assets.getSounds().getMusic(name);
        _coordinator.getSoundsPlayer().playMusicNoLoop(music);
    }

    public void stopMusic(Sounds.Name name){
        Music music = _assets.getSounds().getMusic(name);
        _coordinator.getSoundsPlayer().stopMusic(music);
    }


    public void playSounds(Sounds.Name name){
        Sound sound =  _assets.getSounds().getSound(name);
        _coordinator.getSoundsPlayer().playSound(sound);
    }

    public void playSoundLoop(Sounds.Name name){
        Sound sound =  _assets.getSounds().getSound(name);
        _coordinator.getSoundsPlayer().playSoundLoop(sound);
        _loopingSounds.add(sound);
    }

    public void stopSoundLoop(Sounds.Name name){
        Sound sound =  _assets.getSounds().getSound(name);
        _coordinator.getSoundsPlayer().stopSoundLoop(sound);
        _loopingSounds.remove(sound);
    }

    public void stopAllLoopingSounds(){
        for(Sound sound : _loopingSounds){
            _coordinator.getSoundsPlayer().stopSoundLoop(sound);
        }
        _loopingSounds.clear();
    }

    public void playSounds(Sound sound){
        _coordinator.getSoundsPlayer().playSound(sound);
    }



    @Override
    public void dispose() {
        stopAllLoopingSounds();
        _coordinator.getSoundsPlayer().disposeAllExternalSounds();
    }
}
