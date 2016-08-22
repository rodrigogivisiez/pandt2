package com.mygdx.potatoandtomato.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public class Sizes {

    public static Vector2 resize(float finalWidth, Texture texture){
        float percent = texture.getWidth() / finalWidth;
        return new Vector2(finalWidth, texture.getHeight() / percent);
    }

    public static Vector2 resize(float finalWidth, TextureRegion textureRegion){
        float percent = textureRegion.getRegionWidth() / finalWidth;
        return new Vector2(finalWidth, textureRegion.getRegionHeight() / percent);
    }

    public static Vector2 resizeByH(float finalHeight, TextureRegion textureRegion){
        float percent = textureRegion.getRegionHeight() / finalHeight;
        return new Vector2(textureRegion.getRegionWidth() / percent, finalHeight);
    }

    public static Vector2 resizeByWidthWithMaxWidth(float maxWidth, TextureRegion textureRegion){
        if(textureRegion.getRegionWidth() > maxWidth){
            return resize(maxWidth, textureRegion);
        }
        else{
            return new Vector2(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        }
    }

    public static Vector2 resizeByWidthWithMaxWidth(float maxWidth, Texture texture){
        if(texture.getWidth() > maxWidth){
            return resize(maxWidth, texture);
        }
        else{
            return new Vector2(texture.getWidth(), texture.getHeight());
        }
    }

}
