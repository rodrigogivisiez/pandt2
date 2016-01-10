package com.potatoandtomato.common;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class Player {

    public String name;
    public String userId;
    public boolean isMe;
    public boolean isHost;

    public Player(String name, String userId, boolean isMe, boolean isHost) {
        this.name = name;
        this.userId = userId;
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

    public boolean isMe() {
        return isMe;
    }
}
