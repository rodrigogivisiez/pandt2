package com.potatoandtomato.games.models;

import com.potatoandtomato.games.helpers.*;
import com.potatoandtomato.games.references.BattleRef;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class Services {

    private Assets assets;
    private Texts texts;
    private SoundsWrapper soundsWrapper;
    private Database database;

    public Services(Assets assets, Texts texts, SoundsWrapper soundsWrapper, Database database) {
        this.texts = texts;
        this.assets = assets;
        this.soundsWrapper = soundsWrapper;
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public SoundsWrapper getSoundsWrapper() {
        return soundsWrapper;
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
