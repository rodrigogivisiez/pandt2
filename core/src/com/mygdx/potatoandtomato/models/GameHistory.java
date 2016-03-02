package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.helpers.utils.DateTimes;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by SiongLeng on 24/12/2015.
 */
public class GameHistory {

    private String nameOfGame;
    private Profile playedWith;
    private Long creationDate;


    public String getNameOfGame() {
        return nameOfGame;
    }

    public void setNameOfGame(String nameOfGame) {
        this.nameOfGame = nameOfGame;
    }

    public long getCreationDate() {
        return System.currentTimeMillis() / 1000L;
    }

    @JsonIgnore
    public Long getCreationDateLong() {
        return creationDate;
    }

    @JsonIgnore
    public String getCreationDateAgo() {
        return DateTimes.calculateTimeAgo(creationDate);
    }


    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Profile getPlayedWith() {
        return playedWith;
    }

    public void setPlayedWith(Profile playedWith) {
        this.playedWith = playedWith;
    }
}
