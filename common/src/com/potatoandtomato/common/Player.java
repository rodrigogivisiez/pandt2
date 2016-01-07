package com.potatoandtomato.common;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class Player {

    public String name;
    public String userId;
    public int potatoOrTomato;
    public boolean isMe;
    public boolean isHost;

    public Player(String name, String userId, int potatoOrTomato, boolean isMe, boolean isHost) {
        this.name = name;
        this.userId = userId;
        this.potatoOrTomato = potatoOrTomato;
        this.isMe = isMe;
        this.isHost = isHost;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPotatoOrTomato() {
        return potatoOrTomato;
    }

    public void setPotatoOrTomato(int potatoOrTomato) {
        this.potatoOrTomato = potatoOrTomato;
    }

    public boolean isMe() {
        return isMe;
    }
}
