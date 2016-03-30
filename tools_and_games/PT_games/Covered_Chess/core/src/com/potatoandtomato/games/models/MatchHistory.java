package com.potatoandtomato.games.models;

/**
 * Created by SiongLeng on 24/3/2016.
 */
public class MatchHistory {

    private String opponentUserId;
    private boolean won;

    public MatchHistory() {
    }

    public MatchHistory(String opponentUserId, boolean won) {
        this.opponentUserId = opponentUserId;
        this.won = won;
    }

    public String getOpponentUserId() {
        return opponentUserId;
    }

    public void setOpponentUserId(String opponentUserId) {
        this.opponentUserId = opponentUserId;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }
}
