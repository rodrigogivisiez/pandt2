package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by SiongLeng on 1/2/2016.
 */
public class CoordConverter {

    //original is the size of picture in PhotoHuntCreator, final is the size of picture in game
    public static Vector2 convert(int x, int y, int originalWidth, int originalHeight, int finalWidth, int finalHeight){
        int finalX = (finalWidth * x) / originalWidth;
        int finalY = (finalHeight * y) / originalHeight;

        return new Vector2(finalX, finalY);
    }

    public static Vector2 convert(int x, int y, float originalWidth, float originalHeight, float finalWidth, float finalHeight){
        return convert(x, y, (int) originalWidth, (int) originalHeight, (int) finalWidth, (int) finalHeight);
    }

}
