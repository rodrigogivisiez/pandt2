package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.assets.AnimationAssets;

/**
 * Created by SiongLeng on 12/4/2016.
 */
public class Animations extends AnimationAssets {


    public Animations(PTAssetsManager assetManager) {
        super(assetManager);
    }

    public void disposeAnimation(Name name) {
        disposeAnimation(name.name());
    }

    public enum Name{
        KNIGHT_WALK, KNIGHT_RUN, KNIGHT_ATK, KNIGHT_WON,
        KING_NORMAL, KING_PANIC, KING_WIN, KING_LOSE,
        KNIGHT_HANDUP
    }



}
