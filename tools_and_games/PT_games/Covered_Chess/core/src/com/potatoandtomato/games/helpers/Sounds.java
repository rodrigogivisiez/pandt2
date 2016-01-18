package com.potatoandtomato.games.helpers;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class Sounds {

    private Assets _assets;

    public Sounds(Assets _assets) {
        this._assets = _assets;
    }

    public void playTheme(){
        _assets.getThemeMusic().setLooping(true);
        _assets.getThemeMusic().play();
    }

    public void stopTheme(){
        _assets.getThemeMusic().stop();
    }

}
