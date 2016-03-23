package com.potatoandtomato.games.models;

/**
 * Created by SiongLeng on 20/2/2016.
 */
public class TerrainModel {

    public int col, row;
    public boolean breaking, broken;
    public boolean percentShown, selected;

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

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public boolean isBreaking() {
        return breaking;
    }

    public void setBreaking(boolean breaking) {
        this.breaking = breaking;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isPercentShown() {
        return percentShown;
    }

    public void setPercentShown(boolean percentShown) {
        this.percentShown = percentShown;
    }
}
