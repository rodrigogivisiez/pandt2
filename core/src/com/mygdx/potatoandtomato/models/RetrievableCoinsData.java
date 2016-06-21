package com.mygdx.potatoandtomato.models;

/**
 * Created by SiongLeng on 20/6/2016.
 */
public class RetrievableCoinsData {

    private int canRetrieveCoinsCount;
    private int nextCoinInSecs;
    private int maxRetrieveableCoins;
    private int secsPerCoin;

    public RetrievableCoinsData() {
    }

    public int getSecsPerCoin() {
        return secsPerCoin;
    }

    public void setSecsPerCoin(int secsPerCoin) {
        this.secsPerCoin = secsPerCoin;
    }

    public int getMaxRetrieveableCoins() {
        return maxRetrieveableCoins;
    }

    public void setMaxRetrieveableCoins(int maxRetrieveableCoins) {
        this.maxRetrieveableCoins = maxRetrieveableCoins;
    }

    public int getCanRetrieveCoinsCount() {
        return canRetrieveCoinsCount;
    }

    public void setCanRetrieveCoinsCount(int canRetrieveCoinsCount) {
        this.canRetrieveCoinsCount = canRetrieveCoinsCount;
    }

    public int getNextCoinInSecs() {
        return nextCoinInSecs;
    }

    public void setNextCoinInSecs(int nextCoinInSecs) {
        this.nextCoinInSecs = nextCoinInSecs;
    }
}
