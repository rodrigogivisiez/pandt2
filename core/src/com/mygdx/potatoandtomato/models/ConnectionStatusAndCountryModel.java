package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.enums.GameConnectionStatus;

/**
 * Created by SiongLeng on 16/8/2016.
 */
public class ConnectionStatusAndCountryModel {

    private GameConnectionStatus gameConnectionStatus;
    private String country;

    public ConnectionStatusAndCountryModel() {
    }

    public ConnectionStatusAndCountryModel(String country, GameConnectionStatus gameConnectionStatus) {
        this.country = country;
        this.gameConnectionStatus = gameConnectionStatus;
    }

    public GameConnectionStatus getGameConnectionStatus() {
        return gameConnectionStatus;
    }

    public void setGameConnectionStatus(GameConnectionStatus gameConnectionStatus) {
        this.gameConnectionStatus = gameConnectionStatus;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
