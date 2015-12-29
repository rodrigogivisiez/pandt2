package com.potatoandtomato.games.helpers;

/**
 * Created by SiongLeng on 29/12/2015.
 */
public class Positions {

    public static float centerY(float fullHeight, float textureHeight){
        return (float) fullHeight / 2 - textureHeight / 2;
    }

    public static float centerX(float fullWidth, float textureWidth){
        return (float) fullWidth / 2 - textureWidth / 2;
    }

}
