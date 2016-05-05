package com.potatoandtomato.games.models;

import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.services.Database;
import com.potatoandtomato.games.services.RoomMsgHandler;
import com.potatoandtomato.games.services.SoundsWrapper;
import com.potatoandtomato.games.services.Texts;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class Services implements Disposable {

    private MyAssets assets;
    private SoundsWrapper soundsWrapper;
    private Database database;
    private Texts texts;
    private RoomMsgHandler roomMsgHandler;

    public Services(MyAssets assets, SoundsWrapper soundsWrapper, Database database, Texts texts, RoomMsgHandler roomMsgHandler) {
        this.assets = assets;
        this.soundsWrapper = soundsWrapper;
        this.database = database;
        this.texts = texts;
        this.roomMsgHandler = roomMsgHandler;
    }

    public MyAssets getAssets() {
        return assets;
    }

    public SoundsWrapper getSoundsWrapper() {
        return soundsWrapper;
    }

    public Database getDatabase() {
        return database;
    }

    public Texts getTexts() {
        return texts;
    }

    public RoomMsgHandler getRoomMsgHandler() {
        return roomMsgHandler;
    }

    public void setRoomMsgHandler(RoomMsgHandler roomMsgHandler) {
        this.roomMsgHandler = roomMsgHandler;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    @Override
    public void dispose() {
        assets.dispose();
        soundsWrapper.dispose();
    }
}
