package com.potatoandtomato.games.models;

import com.potatoandtomato.games.helpers.*;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class Services {

    private Assets assets;
    private Texts texts;
    private Sounds sounds;
    private BattleReference battleReference;
    private Database database;

    public Services(Assets assets, Texts texts, Sounds sounds, BattleReference battleReference, Database database) {
        this.texts = texts;
        this.assets = assets;
        this.sounds = sounds;
        this.battleReference = battleReference;
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public Sounds getSounds() {
        return sounds;
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

    public BattleReference getBattleReference() {
        return battleReference;
    }

    public void setBattleReference(BattleReference battleReference) {
        this.battleReference = battleReference;
    }
}
