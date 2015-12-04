package com.mygdx.potatoandtomato.helpers.utils;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public class Positions {

    private static int _height = 640;
    private static int _width = 360;
    public static int getHeight() { return _height; }
    public static int getWidth() { return _width; }
    public static void setWidth(int _width) { Positions._width = _width; }
    public static void setHeight(int _height) { Positions._height = _height; }

    public static float centerX(int textureWidth){
        return centerX((float) textureWidth);
    }

    public static float centerX(float textureWidth){
        return (float) getWidth() / 2 - textureWidth / 2;
    }

}
