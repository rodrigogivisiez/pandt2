package com.potatoandtomato.games.models;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class CorrectArea {

    private int topLeftX;
    private int bottomRightX;
    private int topLeftY;
    private int bottomRightY;

    public CorrectArea() {
    }

    public void setTopLeftX(int topLeftX) {
        this.topLeftX = topLeftX;
    }

    public void setBottomRightX(int bottomRightX) {
        this.bottomRightX = bottomRightX;
    }

    public void setTopLeftY(int topLeftY) {
        this.topLeftY = topLeftY;
    }

    public void setBottomRightY(int bottomRightY) {
        this.bottomRightY = bottomRightY;
    }

    public int getTopLeftX() {
        return topLeftX;
    }

    public int getBottomRightX() {
        return bottomRightX;
    }

    public int getTopLeftY() {
        return topLeftY;
    }

    public int getBottomRightY() {
        return bottomRightY;
    }
}
