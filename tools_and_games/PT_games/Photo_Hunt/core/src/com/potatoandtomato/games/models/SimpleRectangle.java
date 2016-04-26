package com.potatoandtomato.games.models;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by SiongLeng on 7/4/2016.
 */
public class SimpleRectangle {

    private float width;
    private float height;
    private float x;
    private float y;
    private String userId;

    public SimpleRectangle(float width, float height, float x, float y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public SimpleRectangle() {
    }

    public SimpleRectangle(Rectangle rectangle) {
        this.width = rectangle.getWidth();
        this.height = rectangle.getHeight();
        this.x = rectangle.getX();
        this.y = rectangle.getY();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
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

    @Override
    public boolean equals(Object o) {
        if(o instanceof SimpleRectangle){
            SimpleRectangle rectangle = (SimpleRectangle) o;
            return this.getX() == rectangle.getX() &&
                    this.getY() == rectangle.getY() &&
                    this.getHeight() == rectangle.getHeight() &&
                    this.getWidth() == rectangle.getWidth();
        }
        else{
            return super.equals(o);
        }
    }
}
