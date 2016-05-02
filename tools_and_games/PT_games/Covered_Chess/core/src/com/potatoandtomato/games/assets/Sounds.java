package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.assets.SoundAssets;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class Sounds extends SoundAssets {

    public Sounds(AssetManager _manager) {
        super(_manager);
    }

    public enum Name{
        START_GAME, OPEN_SLIDE,
        FLIP_CHESS, MOVE_CHESS,
        FIGHT_CHESS, WIN, LOSE,
        GLASS_CRACKING, GLASS_BROKEN,
        DROPPING, THEME_MUSIC, THUNDER, THEME_SUDDEN_D_MUSIC,
        PARALYZED, POISON, ANGRY, DECREASE, KING, HEAL, DEFEND_SUCCESS
    }

}
