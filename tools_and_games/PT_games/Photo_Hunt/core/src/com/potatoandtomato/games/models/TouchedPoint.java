package com.potatoandtomato.games.models;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class TouchedPoint {

    public float x;
    public float y;
    public int remainingMiliSecs;
    public int hintLeft;
    public SimpleRectangle correctRect;

    public TouchedPoint(float x, float y, int remainingMiliSecs, int hintLeft, SimpleRectangle correctRect) {
        this.x = x;
        this.y = y;
        this.remainingMiliSecs = remainingMiliSecs;
        this.hintLeft = hintLeft;
        this.correctRect = correctRect;
    }

    public TouchedPoint() {
    }

    public SimpleRectangle getCorrectRect() {
        return correctRect;
    }

    public void setCorrectRect(SimpleRectangle correctRect) {
        this.correctRect = correctRect;
    }

    public int getHintLeft() {
        return hintLeft;
    }

    public void setHintLeft(int hintLeft) {
        this.hintLeft = hintLeft;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getRemainingMiliSecs() {
        return remainingMiliSecs;
    }

    public void setRemainingMiliSecs(int remainingMiliSecs) {
        this.remainingMiliSecs = remainingMiliSecs;
    }
}
