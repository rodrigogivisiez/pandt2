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
    private ScoresHelper scoresHelper;

    public Services(Assets assets, Texts texts, SoundsWrapper soundsWrapper, Database database, ScoresHelper scoresHelper) {
        this.assets = assets;
        this.texts = texts;
        this.soundsWrapper = soundsWrapper;
        this.database = database;
        this.scoresHelper = scoresHelper;
    }

    public ScoresHelper getScoresHelper() {
        return scoresHelper;
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
