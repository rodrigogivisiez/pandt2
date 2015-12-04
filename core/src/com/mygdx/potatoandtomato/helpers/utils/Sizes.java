package com.mygdx.potatoandtomato.helpers.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public class Sizes {

    public static Vector2 resize(float finalWidth, TextureRegion textureRegion){
        float percent = textureRegion.getRegionWidth() / finalWidth;
        return new Vector2(finalWidth, textureRegion.getRegionHeight() / percent);

    }



}
