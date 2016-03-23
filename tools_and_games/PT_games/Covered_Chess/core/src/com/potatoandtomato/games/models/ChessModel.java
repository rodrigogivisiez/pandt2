package com.potatoandtomato.games.models;

import com.potatoandtomato.games.absint.Model;
import com.potatoandtomato.games.enums.ChessAnimal;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.enums.Status;
import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by SiongLeng on 19/2/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChessModel extends Model {

    public ChessType chessType;
    public boolean opened;
    public boolean selected;
    public boolean dragging;
    public boolean focusing;
    public Status status;
    public int statusTurn;
    public int killCount;

    public ChessModel() {
        this.status = Status.NONE;
    }

    public ChessModel(ChessType chessType) {
        this.chessType = chessType;
        this.status = Status.NONE;
    }

    public int getKillCount() {
        return killCount;
    }

    public void setKillCount(int killCount) {
        this.killCount = killCount;
    }

    public void addKillCount(){
        this.killCount++;
    }

    public boolean canTransform(){
        return this.killCount == 3 && this.status != Status.KING;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.statusTurn = 0;
    }

    public int getStatusTurn() {
        return statusTurn;
    }

    public void setStatusTurn(int statusTurn) {
        this.statusTurn = statusTurn;
    }

    public void addStatusTurn(){
        this.statusTurn++;
    }

    public ChessType getChessType() {
        return chessType;
    }

    public void setChessType(ChessType chessType) {
        this.chessType = chessType;
    }

    public boolean getOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isYellow(){ return chessType.name().startsWith("YELLOW"); }

    public boolean isRed(){ return chessType.name().startsWith("RED"); }

    public ChessColor getChessColor() {
        if(chessType.name().startsWith("YELLOW")) return ChessColor.YELLOW;
        else return ChessColor.RED;
    }

    public boolean getDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public boolean getFocusing() {
        return focusing;
    }

    public void setFocusing(boolean focusing) {
        this.focusing = focusing;
    }

    public ChessAnimal getChessAnimal(){
        if(this.getChessType() != null){
            return this.getChessType().toChessAnimal();
        }
        return ChessAnimal.NONE;
    }

    public ChessModel clone(){
        ChessModel chessModel = new ChessModel(this.chessType);
        chessModel.setDragging(this.getDragging());
        chessModel.setSelected(this.getSelected());
        chessModel.setOpened(this.getOpened());
        chessModel.setFocusing(this.getFocusing());
        chessModel.setStatus(this.getStatus());
        chessModel.setStatusTurn(this.getStatusTurn());
        chessModel.setKillCount(this.getKillCount());
        return chessModel;
    }

    public void resetSurface(){
        setFocusing(false);
        setDragging(false);
        setSelected(false);
    }

}
