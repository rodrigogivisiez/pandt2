package com.potatoandtomato.games.models;

import com.potatoandtomato.games.absint.Model;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
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

    public ChessModel() {
    }

    public ChessModel(ChessType chessType) {
        this.chessType = chessType;
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

    public ChessModel clone(){
        ChessModel chessModel = new ChessModel(this.chessType);
        chessModel.setDragging(this.getDragging());
        chessModel.setSelected(this.getSelected());
        chessModel.setOpened(this.getOpened());
        chessModel.setFocusing(this.getFocusing());
        return chessModel;
    }

}
