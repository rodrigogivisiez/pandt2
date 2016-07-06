package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.assets.SoundAssets;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Sounds extends SoundAssets {

    public Sounds(PTAssetsManager _manager) {
        super(_manager);
    }

    public enum Name{
        BUTTON_CLICKED, TOGETHER_CHEERS,
        SLIDING, GAME_CREATED, MIC, COUNT_DOWN, MESSAGING,
        THEME_MUSIC, SHOP_MUSIC,
        TOGETHER_HAPPY, TOGETHER_BORED, TOGETHER_FAILED, TOGETHER_CRY, TOGETHER_ANTICIPATING,
        MOVING_RANK_END, STREAK_DIED, ADDING_SCORE, MOVING_RANK, STREAK, SCORE_APPEAR,
        TUTORIAL, WIN, LOSE, CLICK_BUTTON, APPEAR, COIN_FLIP, FINISH_PLAY_AUDIO_MSG,
        CHECKBOX_SOUND, WOOD_BTN_CLICK, OPEN_POPUP,
        COIN_PURCHASED, COIN_ADDING, WRONG,
    }
}
