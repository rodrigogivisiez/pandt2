package com.potatoandtomato.games.models;

import com.potatoandtomato.games.actors.chesses.enums.ChessType;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 31/12/2015.
 */
public class GameInfo {

    public boolean yellowTurn;
    public String chessInfo;
    public ArrayList<ChessType> chessTypes;

    public GameInfo() {
        chessTypes = new ArrayList<ChessType>();
    }

    public String getChessInfo() {
        return chessInfo;
    }

    public void setChessInfo(String chessInfo) {
        this.chessInfo = chessInfo;
    }

    public ArrayList<ChessType> getChessTypes() {
        if(chessTypes.size() == 0){
            ChessType[] types = ChessType.values();

            String[] chessInfoArr = chessInfo.split(",");
            for(String info : chessInfoArr){
                addChessType(types[Integer.valueOf(info)]);
            }
        }
        return chessTypes;
    }

    public void addChessType(ChessType chess) {
        chessTypes.add(chess);
    }

    public boolean isYellowTurn() {
        return yellowTurn;
    }

    public void setYellowTurn(boolean yellowTurn) {
        this.yellowTurn = yellowTurn;
    }
}
