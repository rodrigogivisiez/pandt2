package com.potatoandtomato.games.models;

import com.potatoandtomato.games.absint.Model;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by SiongLeng on 19/2/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraveModel extends Model {

    private ArrayList<ChessType> graveChesses;
    private int yellowLeftTime;
    private int redLeftTime;

    public GraveModel() {
        this.graveChesses = new ArrayList<ChessType>();
        this.redLeftTime = this.yellowLeftTime = 900;
    }

    public int getRedLeftTime() {
        return redLeftTime;
    }

    public void setRedLeftTime(int redLeftTime) {
        this.redLeftTime = redLeftTime;
    }

    public int getYellowLeftTime() {
        return yellowLeftTime;
    }

    public void setYellowLeftTime(int yellowLeftTime) {
        this.yellowLeftTime = yellowLeftTime;
    }

    public void minusTimeLeft(ChessColor chessColor){
        if(chessColor == ChessColor.YELLOW){
            yellowLeftTime--;
        }
        if(chessColor == ChessColor.RED){
            redLeftTime--;
        }
        if(yellowLeftTime < 0) yellowLeftTime = 0;
        if(redLeftTime < 0) redLeftTime = 0;
    }

    public Integer getLeftTimeInt(ChessColor chessColor){
        int sec = chessColor == ChessColor.YELLOW ? yellowLeftTime : redLeftTime;
        return sec;
    }

    public String getLeftTime(ChessColor chessColor){
        int sec = chessColor == ChessColor.YELLOW ? yellowLeftTime : redLeftTime;

        int minutes = sec / 60;
        int seconds = sec % 60;
        return minutes + ":" + String.format("%02d", seconds);
    }

    public GraveModel(ArrayList<ChessType> graveChesses) {
        this.graveChesses = graveChesses;
    }

    public ArrayList<ChessType> getGraveChesses() {
        return graveChesses;
    }

    public void setGraveChesses(ArrayList<ChessType> graveChesses) {
        this.graveChesses = graveChesses;
    }

    public void addToGrave(ChessType chessType){
        graveChesses.add(chessType);
        Collections.sort(graveChesses);
    }

    public int getLeftChessCountByColor(ChessColor color){
        int count = 16;
        if(color == ChessColor.YELLOW){
            for(ChessType chessType : getGraveChesses()){
                if(chessType.name().startsWith("YELLOW")) count--;
            }
        }
        else if(color == ChessColor.RED){
            for(ChessType chessType : getGraveChesses()){
                if(chessType.name().startsWith("RED")) count--;
            }
        }
        return count;
    }

}
