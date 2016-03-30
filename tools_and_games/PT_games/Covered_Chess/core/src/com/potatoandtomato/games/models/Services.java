package com.potatoandtomato.games.models;

import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.services.*;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class Services {

    private MyAssets assets;
    private Texts texts;
    private SoundsWrapper soundsWrapper;
    private Database database;
    private ScoresHandler scoresHandler;
    private GameDataController gameDataController;

    public Services(MyAssets assets, Texts texts, SoundsWrapper soundsWrapper, Database database, ScoresHandler scoresHandler, GameDataController gameDataController) {
        this.assets = assets;
        this.texts = texts;
        this.soundsWrapper = soundsWrapper;
        this.database = database;
        this.scoresHandler = scoresHandler;
        this.gameDataController = gameDataController;
    }

    public GameDataController getGameDataController() {
        return gameDataController;
    }

    public void setGameDataController(GameDataController gameDataController) {
        this.gameDataController = gameDataController;
    }

    public ScoresHandler getScoresHandler() {
        return scoresHandler;
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

    public MyAssets getAssets() {
        return assets;
    }

    public void setAssets(MyAssets assets) {
        this.assets = assets;
    }

}
