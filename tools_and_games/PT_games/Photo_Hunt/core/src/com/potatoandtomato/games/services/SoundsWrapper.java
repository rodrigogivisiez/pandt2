package com.potatoandtomato.games.services;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.games.assets.Sounds;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class SoundsWrapper implements Disposable {

    private Assets _assets;
    private GameCoordinator _coordinator;
    private Music _themeMusic;
    private Music _themeSuddenDMusic;

    public SoundsWrapper(Assets _assets, GameCoordinator coordinator) {
        this._assets = _assets;
        this._coordinator = coordinator;

//        _themeMusic = _assets.getSounds().getMusic(Sounds.Name.THEME_MUSIC);
//        _themeMusic.setLooping(true);
//
//        _themeSuddenDMusic = _assets.getSounds().getMusic(Sounds.Name.THEME_SUDDEN_D_MUSIC);
//        _themeSuddenDMusic.setLooping(true);
//
//        _coordinator.getSoundsPlayer().addMusic(_themeMusic);
//        _coordinator.getSoundsPlayer().addMusic(_themeSuddenDMusic);
    }

    public void playTheme(){
        _coordinator.getSoundsPlayer().playMusic(_themeMusic);
    }

    public void playThemeMusicSuddenD(){
        _coordinator.getSoundsPlayer().playMusic(_themeSuddenDMusic);
    }


    public void stopTheme(){
        _themeMusic.stop();
        _themeSuddenDMusic.stop();
    }

    public void playSounds(Sounds.Name name){
        Sound sound =  _assets.getSounds().getSound(name);
        _coordinator.getSoundsPlayer().playSound(sound);
    }

    @Override
    public void dispose() {
        _coordinator.getSoundsPlayer().disposeMusic(_themeMusic);
        _coordinator.getSoundsPlayer().disposeMusic(_themeSuddenDMusic);
    }
}
