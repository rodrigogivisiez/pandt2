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
        CIRCLE,
        EMPTY,
        TOP_BG, TOP_BG_SHADOW,
        BOTTOM_BG, BOTTOM_BG_SHADOW, CASTLE_ROOM,
        HINT_ON_ICON, HINT_OFF_ICON,
        TRANS_BLACK_BG, CURSOR_BLACK
    }



}
