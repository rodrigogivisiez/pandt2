package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.assets.TextureAssets;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.enums.Status;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Textures extends TextureAssets {

    public Textures(PTAssetsManager _manager, String packPath) {
        super(_manager, packPath);
    }

    public TextureRegion getAnimalByType(ChessType chessType){
        String chessTypeString = chessType.name();
        return get(chessTypeString);
    }


    public TextureRegion getStatus(Status status){
        return get("STATUS_" + status.name());
    }

    public enum Name{
        ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT, ARROW_UP, ARROW_BOTTOM_LEFT, ARROW_TOP_LEFT, ARROW_BOTTOM_RIGHT, ARROW_TOP_RIGHT,
        GREEN_TILE, RED_TILE, GLOWING_TILE,
        SUDDEN_DEATH_GAME_BG, GAME_BG, TRANS_DARK_BROWN_ROUNDED_BG, GRAVE_BG, TRANS_BLACK_BG, BLACK_BG, SPLASH_BG, FULL_BLACK,
        POINT_LEFT_ICON, POINT_RIGHT_ICON, PREVIEW_ICON, GRAVE_ICON, TUTORIAL_ICON, CLOSE_ICON,
        GLOW_CHESS, RED_CHESS_TOTAL, YELLOW_CHESS_TOTAL, CHESS_SHIELD, STAR,
        BATTLE_EFFECT, BATTLE_CLOUD,
        EMPTY,
        YELLOW_CHESS, RED_CHESS, UNKNOWN_CHESS, YELLOW_CHESS_SELECTED, RED_CHESS_SELECTED, UNKNOWN_CHESS_SELECTED,
        PREVIEW_CHESS,
        YELLOW_LION_SPLASH, RED_LION_SPLASH, YELLOW_TIGER_SPLASH, RED_TIGER_SPLASH, YELLOW_WOLF_SPLASH, RED_WOLF_SPLASH,
        CRACK,
        YOU_LOSE_YELLOW, YOU_WIN_YELLOW, YOU_LOSE_RED, YOU_WIN_RED,
        TUTORIAL_BG, FOOD_CHAIN, GREY_HORIZONTAL_LINE, SWIPE_TO_OPEN, DRAG_TUTORIAL,
        MOUSE_TUTORIAL, CAT_TUTORIAL, DOG_TUTORIAL, WOLF_TUTORIAL, TIGER_TUTORIAL, LION_TUTORIAL, ELEPHANT_TUTORIAL
    }



}
