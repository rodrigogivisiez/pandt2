package com.mygdx.potatoandtomato.models;

import com.potatoandtomato.common.models.EndGameResult;

/**
 * Created by SiongLeng on 14/3/2016.
 */
public class EndGameData {

    EndGameResult endGameResult;
    Room room;
    String userId;


    public EndGameData(Room room, String userId) {
        this.room = room;
        this.userId = userId;
    }

    public EndGameData() {
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public EndGameResult getEndGameResult() {
        return endGameResult;
    }

    public void setEndGameResult(EndGameResult endGameResult) {
        this.endGameResult = endGameResult;
    }

}
