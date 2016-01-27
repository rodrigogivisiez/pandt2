package com.potatoandtomato.common;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class Player {

    public String name;
    public String userId;
    public boolean isHost;

    public Player() {
    }

    public Player(String name, String userId, boolean isHost) {
        this.name = name;
        this.userId = userId;
        this.isHost = isHost;
    }

    public boolean getIsHost() {
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
}
