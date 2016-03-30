package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.potatoandtomato.common.assets.AnimationAssets;

/**
 * Created by SiongLeng on 9/2/2016.
 */
public class Animations extends AnimationAssets {

    public Animations(AssetManager assetManager) {
        super(assetManager);
    }

    public enum Name{
        LOADING,
        POTATO_BORING, POTATO_FAILED, POTATO_CRY, POTATO_HAPPY, POTATO_ANTICIPATE,
        TOMATO_BORING, TOMATO_FAILED, TOMATO_CRY, TOMATO_HAPPY, TOMATO_ANTICIPATE
    }


}
