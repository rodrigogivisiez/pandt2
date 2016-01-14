package com.potatoandtomato.games.models;

import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.Texts;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class Services {

    private Assets assets;
    private Texts texts;

    public Services(Assets assets, Texts texts) {
        this.texts = texts;
        this.assets = assets;
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

}
