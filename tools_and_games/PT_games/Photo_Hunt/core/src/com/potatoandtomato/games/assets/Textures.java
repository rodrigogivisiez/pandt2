package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.potatoandtomato.common.assets.TextureAssets;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Textures extends TextureAssets {

    public Textures(AssetManager _manager, String packPath) {
        super(_manager, packPath);
    }

    public TextureRegion getPlayerCountBg(int playerIndex){
        return this.get(Name.PLAYER$1_COUNTER.name().replace("$1", String.valueOf(playerIndex)));
    }

    public enum Name{
        EMPTY,
        TOP_BG, TOP_BG_SHADOW, HINT_BLOCK,
        BOTTOM_BG, BOTTOM_BG_SHADOW, CASTLE_ROOM,
        HINT_ON_ICON, HINT_OFF_ICON, SMALL_STAR_ICON, STOP_ICON,
        TRANS_BLACK_BG, CURSOR_BLACK,
        CASTLE_DOOR, SHIELD_SWORD, SCRATCH_SEPARATOR, STAR,
        CIRCLE, CROSS,
        PLAYER$1_COUNTER,
        ICE_BOTTOM_HALF, ICE_TOP_HALF,
        DOOR_LEFT, DOOR_RIGHT, DOOR_OVERLAY,
        BULB_BROKEN, BULB_LIGHT_OFF, BULB_LIGHT_ON, FULL_BLACK_BG,
        VIGNETTE,
        MONSTERS_INVERT, MONSTERS,
        CAT, GIRAFFE, VAMPIRE, VAN, BIRTHDAY, SANTA_LEFT, DEER_RIGHT, SEXY_GIRL, MAN_GIVE_FLOWER,
        MAN_RUNNING, PREGNANT, CROWD, SPEECH, ROCKET, UFO, ALIEN,
        WRINKLE_BG, COVERED, EGG, YOLK,
        WHITE_BG
    }



}
