package com.potatoandtomato.games.models;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class TouchedPoint {

    public float x;
    public float y;
    public int remainingSecs;

    public TouchedPoint(float x, float y, int remainingSecs) {
        this.x = x;
        this.y = y;
        this.remainingSecs = remainingSecs;
    }

    public TouchedPoint() {
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

    public int getRemainingSecs() {
        return remainingSecs;
    }

    public void setRemainingSecs(int remainingSecs) {
        this.remainingSecs = remainingSecs;
    }
}
