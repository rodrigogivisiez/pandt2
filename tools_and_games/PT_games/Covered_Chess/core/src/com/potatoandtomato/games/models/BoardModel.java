package com.potatoandtomato.games.models;

import com.potatoandtomato.games.absint.Model;
import com.potatoandtomato.games.enums.ChessColor;
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
        return getAccTurnCount() >= 50;
    }

    public void setSuddenDeath(boolean suddenDeath) {
        this.suddenDeath = suddenDeath;
    }

    public boolean isCrackStarting(){
        return getAccTurnCount() >= 55;
    }

    public boolean isCrackHappened(){
        return getAccTurnCount() >= 65;
    }

    public ChessColor getCurrentTurnChessColor(){
        return currentTurnIndex == 0 ? ChessColor.YELLOW : ChessColor.RED;
    }

    public void switchTurnIndex(){
        this.setCurrentTurnIndex(this.getCurrentTurnIndex() == 0 ? 1 : 0);
        this.accTurnCount++;
    }

}
