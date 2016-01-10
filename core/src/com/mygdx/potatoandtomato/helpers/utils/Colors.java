package com.mygdx.potatoandtomato.helpers.utils;

import com.badlogic.gdx.graphics.Color;

import java.util.Random;

/**
 * Created by SiongLeng on 11/1/2016.
 */
public class Colors {

    public static Color generatePleasingColor() {

        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);


        red = (red + 255) / 2;
        green = (green + 255) / 2;
        blue = (blue + 255) / 2;


        Color color = new Color(red/255f, green/255f, blue/255f, 1);
        return color;
    }
}
