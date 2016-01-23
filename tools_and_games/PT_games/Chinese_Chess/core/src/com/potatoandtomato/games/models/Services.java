package com.potatoandtomato.games.models;

import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.Sounds;
import com.potatoandtomato.games.helpers.Texts;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class Services {

    private Assets assets;
    private Texts texts;
    private Sounds sounds;

    public Services(Assets assets, Texts texts, Sounds sounds) {
        this.texts = texts;
        this.assets = assets;
        this.sounds = sounds;
    }

    public Texts getTexts() {
        return texts;
    }

    public Assets getAssets() {
        return assets;
    }

    public void setAssets(Assets assets) {
        this.assets = assets;
    }

    public Sounds getSounds() {
        return sounds;
    }
}
