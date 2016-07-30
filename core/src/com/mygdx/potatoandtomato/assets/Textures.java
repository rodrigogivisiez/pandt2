package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.assets.TextureAssets;

/**
 * Created by SiongLeng on 9/2/2016.
 */
public class Textures extends TextureAssets {

    public Textures(PTAssetsManager _manager, String packPath) {
        super(_manager, packPath);
    }

    public TextureRegion getArcadeScreen(int number){
        return get("ARCADE_" + number + "_SCREEN");
    }


    public enum Name{
        SAMPLE,
        BLUE_BG, AUTUMN_BG, SUNRISE, SUNRAY, LOGO_NO_WEAPON, LOGO_POTATO_WEAPON, LOGO_TOMATO_WEAPON, GREEN_GROUND, AUTUMN_GROUND,
        TOP_BAR_BG, TRANS_WHITE_BG, TRANS_BLACK_BG, WHITE_ROUND_BUTTON_BG, LEADER_BOARD_BG,
        FULL_BLACK_BG, FULL_WHITE_BG, LESS_TRANS_BLACK_BG,
        UPRIGHT_EGG_BUTTON, DOWNWARD_EGG_BUTTON, ROPE,
        TOMATO_HI, POTATO_HI, LOGGING_IN_MASCOTS,
        PLAY_ICON, FACEBOOK_ICON, TICK_ICON, CROSS_ICON, QUIT_ICON, BACK_ICON, SETTINGS_ICON, RATE_ICON, BACK_ICON_DARK,
        EXPANDED_ICON, COLLAPSED_ICON, KICK_ICON, INVITED_ICON, POINT_LEFT_ICON, VOICE_ICON,
        STREAK_ICON, LEADERBOARD_ICON, LEADERBOARD_MAIN_ICON, RANK_ICON, RECENT_ICON, FACEBOOK_INVITE_ICON,
        LEADERBOARD_INVITE_ICON,
        MIC_BIG_ICON, MIC_ICON, DOWNLOAD_ICON, UNKNOWN_ICON, BULLET_ICON, SMALL_STAR_ICON, DISCONNECTED_ICON,
        NO_IMAGE, EMPTY,
        WOOD_BG_SMALL, WOOD_BG_TALL, WOOD_BG_FAT, WOOD_BG_NORMAL, WOOD_BG_TITLE, WOOD_SEPARATOR_HORIZONTAL,
        WHITE_VERTICAL_LINE, ORANGE_VERTICAL_LINE, ORANGE_HORIZONTAL_LINE, GREY_HORIZONTAL_LINE, WHITE_HORIZONTAL_LINE,
        CURSOR_BLACK,
        NEXT_LEADERBOARD_CLICKED, NEXT_LEADERBOARD_NORMAL, PREV_LEADERBOARD_CLICKED, PREV_LEADERBOARD_NORMAL,
        GAMELIST_HIGHLIGHT, GAMELIST_TITLE_BG,
        LOADING_IMAGE, LOADING_PAGE,
        CHAT_CONTAINER,
        SEND_BTN_ONPRESS, SEND_BTN, CHAT_TEMPLATE_BTN_ONPRESS, CHAT_TEMPLATE_BTN,
        ROOM_USERS_BTN_ONPRESS, ROOM_USERS_BTN, VOICE_BTN_ONPRESS, VOICE_BTN,
        KEYBOARD_BTN, KEYBOARD_BTN_ONPRESS,
        CHAT_POPUP_ARROW, CHAT_POPUP_CLOSE_BTN, WHITE_DOT,
        SCROLLBAR_GREY_HANDLE, SCROLLBAR_GREY_BG, CHAT_TEMPLATE_CHECKBOX, CHAT_TEMPLATE_CHECKBOX_CHECKED,
        SEND_BTN_TRANS, ROOM_USERS_BTN_TRANS, VOICE_BTN_TRANS, CHAT_TEMPLATE_BTN_TRANS, KEYBOARD_BTN_TRANS,
        MIC_WAVE_ONE, MIC_WAVE_TWO, MIC_WAVE_THREE, AUDIO_MSG_WAVE_ONE, AUDIO_MSG_WAVE_TWO, AUDIO_MSG_WAVE_THREE,
        AUDIO_MSG_BG, AUDIO_MSG_TIMELINE,
        SELECT_BOX, UNSELECT_BOX,
        LEADER_BOARD_SEPARATOR,
        TUTORIAL_MASCOT, VERTICAL_DOTS,
        COIN_MACHINE_BG, COIN_MACHINE_BLINK_BG, COIN_MACHINE_HOLE, COIN_MACHINE_ROOT_BG, COIN_MACHINE_SEPARATOR,
        COIN_WITH_ANGLE, COIN_FLAT, COIN_MACHINE_USERS_BG,
        NO_COIN_ICON_SMALL, COIN_ICON_SMALL, NO_COIN_ICON, NO_COIN_ICON_BLACK,
        TOP_BAR_COIN_COUNT,
        FLOOR_BG, SHOP_ARCADE_WORLD, ARCADE_MACHINES, ARCADE_SCREENS ,
        SHOP_TABLE, POTATO_SUNGLASS, TOMATO_SUNGLASS,
        TOMATO_SUNGLASS_LEFT_HAND, TOMATO_SUNGLASS_RIGHT_HAND, PURSE_FRONT, PURSE_BACK,
        COIN_BAG, COIN_FIFTEEN, COIN_FIVE, COIN_ONE, COIN_PURSE, TV_ICON,
        PURSE_COIN_NORMAL, PURSE_COIN_SLEEP, OUT_OF_STOCK_ICON,
        WHITE_BUTTON_PIXEL, WHITE_BUTTON_PIXEL_ONPRESS, RIGHT_BUTTON_PIXEL, LEFT_BUTTON_PIXEL,
        SPEECH_LEFT_PIXEL, SPEECH_RIGHT_PIXEL, MASCOTS_PIXEL, MASCOT_HANDS_PIXEL,
        POTATO_CLOSE_MOUTH, POTATO_OPEN_MOUTH, POTATO_SMILE_MOUTH,
        TOMATO_CLOSE_MOUTH, TOMATO_OPEN_MOUTH, TOMATO_SMILE_MOUTH,
        INSERT_COIN_FRAME_ONE, INSERT_COIN_FRAME_TWO, INSERT_COIN_FRAME_THREE,
        EXTINGUISHER,
        TAP_GESTURE_HAND, TAP_GESTURE_BUTTON, TAP_GESTURE_TRACE,
        FREE_COIN_POINT_RIGHT

    }





}
