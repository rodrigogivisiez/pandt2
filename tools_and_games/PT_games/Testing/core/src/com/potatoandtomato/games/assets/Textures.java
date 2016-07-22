package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.assets.TextureAssets;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Textures extends TextureAssets {

    public Textures(PTAssetsManager _manager, String packPath) {
        super(_manager, packPath);
    }

    public enum Name{
        ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT, ARROW_UP, ARROW_BOTTOM_LEFT, ARROW_TOP_LEFT, ARROW_BOTTOM_RIGHT, ARROW_TOP_RIGHT,
        GREEN_TILE, RED_TILE, GLOWING_TILE,
        SUDDEN_DEATH_GAME_BG, GAME_BG, TRANS_DARK_BROWN_ROUNDED_BG, GRAVE_BG, TRANS_BLACK_BG, BLACK_BG, SPLASH_BG, FULL_BLACK,
        POINT_LEFT_ICON, POINT_RIGHT_ICON, PREVIEW_ICON,
        GLOW_CHESS, RED_CHESS_TOTAL, YELLOW_CHESS_TOTAL,
        BATTLE_EFFECT, BATTLE_CLOUD,
        EMPTY,FULL_WHITE_BG,
        YELLOW_CHESS, RED_CHESS, UNKNOWN_CHESS, YELLOW_CHESS_SELECTED, RED_CHESS_SELECTED, UNKNOWN_CHESS_SELECTED,
        PREVIEW_CHESS,
        YELLOW_LION_SPLASH, RED_LION_SPLASH, YELLOW_TIGER_SPLASH, RED_TIGER_SPLASH, YELLOW_WOLF_SPLASH, RED_WOLF_SPLASH,
        CRACK,
        YOU_LOSE_YELLOW, YOU_WIN_YELLOW, YOU_LOSE_RED, YOU_WIN_RED
    }



}
