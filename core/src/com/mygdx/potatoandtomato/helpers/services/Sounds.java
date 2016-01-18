package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.audio.Music;
import com.mygdx.potatoandtomato.absintflis.sounds.ISounds;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class Sounds implements ISounds {

    private Assets _assets;
    private Music _themeMusic;

    public Sounds(Assets assets) {
        this._assets = assets;
        _themeMusic = _assets.getThemeMusic();
        _themeMusic.setLooping(true);
    }

    @Override
    public void playThemeMusic() {
        _themeMusic.play();
    }

    @Override
    public void stopThemeMusic() {
        _themeMusic.stop();
    }

    @Override
    public void playButtonClicked() {
        _assets.getClickWaterSound().play();
    }
}
