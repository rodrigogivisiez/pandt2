package com.potatoandtomato.games.absintf;

import com.potatoandtomato.games.enums.GameState;

/**
 * Created by SiongLeng on 9/4/2016.
 */
public abstract class GameStateListener {

    public abstract void onChanged(GameState newState);

}
