package com.mygdx.potatoandtomato.models;

/**
 * Created by SiongLeng on 22/6/2016.
 */
public class CoinsMeta {

    public String userId;
    public int coinsCount;

    public CoinsMeta() {
    }

    public CoinsMeta(String userId, int coinsCount) {
        this.userId = userId;
        this.coinsCount = coinsCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCoinsCount() {
        return coinsCount;
    }

    public void setCoinsCount(int coinsCount) {
        this.coinsCount = coinsCount;
    }
}
