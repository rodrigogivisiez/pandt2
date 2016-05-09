package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
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

    public Sound getClickSound(int clickNumber){
        return getSound("CLICK_" + clickNumber);
    }

    public enum Name{
        BONUS_MUSIC,
        HORSE_HOOF, KNIGHT_ATTACKING, KNIGHT_RUN,
        SHOCK, WRONG, ICE, BLACKSMITH, MOVE_ROCK,
        HINT, WIN, ADDING_SCORE, CLOSE_DOOR, OPEN_DOOR,
        START_STAGE, ATTACKING_CASTLE_MUSIC, FALL_DOWN, KNIGHT_WON,
        WRINKLE_PAPER, INVERTED, COVERED_PRESS, TORCH_LIGHT,
        LOOPING, DISALLOW_CLICK, SWITCH_LIGHT, BULB_BREAK,
        MEMORY, MEMORY_END, PUT_EGGS, BREAK_EGG,
        MONSTER_SOUND, MOVE_IN_FOREST, CAT, BOY_LAUGH,
        CAR_REVERSE, CAR_DRIVING, CAR_BRAKE, HAPPY_BIRTHDAY,
        JINGLE_BELL, SEXY_GIRL, BABY, RUNNING, CROWD_APPLAUSE,
        UFO, ROCKET_LAUNCH, ROCKET_SLOW_FLY, CASTLE_DESTROYED, CASTLE_SEMI_DESTROYED

    }

}
