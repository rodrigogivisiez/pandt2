package com.potatoandtomato.games.absint;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.potatoandtomato.games.actors.chesses.ChessActor;
import com.potatoandtomato.games.actors.chesses.enums.ChessType;

/**
 * Created by SiongLeng on 2/1/2016.
 */
public abstract class MainScreenListener {

    public abstract void onChessKilled(ChessType chessType, Drawable animalDrawable, boolean isYellow);

    public abstract void onFinishAction(float delay);

}
