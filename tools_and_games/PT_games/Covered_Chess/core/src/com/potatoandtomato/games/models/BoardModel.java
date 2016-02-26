package com.potatoandtomato.games.models;

import com.potatoandtomato.games.absint.Model;
import com.potatoandtomato.games.screens.TerrainLogic;
import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 25/2/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoardModel extends Model {

    private int currentTurnIndex;
    private int accTurnCount;
    private boolean suddenDeath;

    public BoardModel() {
    }

    public BoardModel(int currentTurnIndex) {
        this.currentTurnIndex = currentTurnIndex;
        this.accTurnCount = 1;
    }

    public int getAccTurnCount() {
        return accTurnCount;
    }

    public void setAccTurnCount(int accTurnCount) {
        this.accTurnCount = accTurnCount;
    }

    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public void setCurrentTurnIndex(int currentTurnIndex) {
        this.currentTurnIndex = currentTurnIndex;
    }

    public boolean isSuddenDeath() {
        return suddenDeath;
    }

    public void setSuddenDeath(boolean suddenDeath) {
        this.suddenDeath = suddenDeath;
    }

    public void switchTurnIndex(){
        this.setCurrentTurnIndex(this.getCurrentTurnIndex() == 0 ? 1 : 0);
        this.accTurnCount++;
    }

}
