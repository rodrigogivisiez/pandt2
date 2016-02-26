package com.potatoandtomato.games.helpers;

import com.potatoandtomato.games.screens.TerrainLogic;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 25/2/2016.
 */
public class Terrains {

    public static TerrainLogic getTerrainLogicByPosition(ArrayList<TerrainLogic> _terrains, int col, int row){
        for(TerrainLogic terrainLogic : _terrains){
            if(terrainLogic.getTerrainModel().getCol() == col && terrainLogic.getTerrainModel().getRow() == row){
                return terrainLogic;
            }
        }
        return null;
    }


}
