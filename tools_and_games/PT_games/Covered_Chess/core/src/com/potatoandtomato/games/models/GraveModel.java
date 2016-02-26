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


    public GraveModel() {
        this.graveChesses = new ArrayList<ChessType>();
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
