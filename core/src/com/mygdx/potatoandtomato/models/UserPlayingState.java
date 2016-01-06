package com.mygdx.potatoandtomato.models;

/**
 * Created by SiongLeng on 7/1/2016.
 */
public class UserPlayingState {

    public String roomId;
    public boolean abandon;
    public boolean connected;

    public UserPlayingState() {
    }

    public UserPlayingState(String roomId, boolean connected) {
        this.roomId = roomId;
        this.connected = connected;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public boolean getAbandon() {
        return abandon;
    }

    public void setAbandon(boolean abandon) {
        this.abandon = abandon;
    }

    public boolean getConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
