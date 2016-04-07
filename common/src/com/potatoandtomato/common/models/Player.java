package com.potatoandtomato.common.models;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class Player {

    public String name;
    public String userId;
    public boolean isHost;
    public boolean isConnected;
    public Color userColor;

    public Player() {
    }

    public Player(String name, String userId, boolean isHost, boolean isConnected, Color userColor) {
        this.name = name;
        this.userId = userId;
        this.isHost = isHost;
        this.isConnected = isConnected;
        this.userColor = userColor;
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
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

    public Color getUserColor() {
        return userColor;
    }

    public void setUserColor(Color userColor) {
        this.userColor = userColor;
    }
}
