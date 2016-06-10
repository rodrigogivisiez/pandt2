package com.mygdx.potatoandtomato.models;

import com.shaded.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by SiongLeng on 7/1/2016.
 */
public class UserPlayingState {

    public String roomId;
    public int roundCounter;

    public UserPlayingState() {
    }

    public UserPlayingState(String roomId, int roundCounter) {
        this.roomId = roomId;
        this.roundCounter = roundCounter;
    }

    public int getRoundCounter() {
        return roundCounter;
    }

    public void setRoundCounter(int roundCounter) {
        this.roundCounter = roundCounter;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @JsonIgnore
    public void abandonGame(){
        roundCounter = 0;
        roomId = "";
    }

    @JsonIgnore
    public boolean canContinue(Room room){
        return room.getId().equals(roomId) && room.getRoundCounter() == roundCounter;
    }
}
