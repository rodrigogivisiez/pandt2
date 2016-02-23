package com.potatoandtomato.games.models;

/**
 * Created by SiongLeng on 20/2/2016.
 */
public class TerrainModel {

    public int col, row;

    public TerrainModel(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
