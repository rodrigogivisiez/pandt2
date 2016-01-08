package com.potatoandtomato.games.models;

import com.potatoandtomato.games.actors.chesses.enums.ChessType;

/**
 * Created by SiongLeng on 9/1/2016.
 */
public class PlateSimple {

    public ChessType chessType;
    public boolean isOpen;
    public boolean isEmpty;

    public PlateSimple(ChessType chessType, boolean isOpen, boolean isEmpty) {
        this.chessType = chessType;
        this.isOpen = isOpen;
        this.isEmpty = isEmpty;
    }

    public ChessType getChessType() {
        return chessType;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
