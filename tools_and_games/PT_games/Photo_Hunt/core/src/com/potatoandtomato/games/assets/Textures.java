package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.assets.TextureAssets;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Textures extends TextureAssets {

    public Textures(AssetManager _manager, String packPath) {
        super(_manager, packPath);
    }

    public enum Name{
        ONE, TWO, CIRCLE,
        EMPTY,
    }



}
