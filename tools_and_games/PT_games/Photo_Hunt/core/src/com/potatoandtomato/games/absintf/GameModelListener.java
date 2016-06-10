package com.potatoandtomato.games.absintf;

import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.SimpleRectangle;

/**
 * Created by SiongLeng on 13/4/2016.
 */
public abstract class GameModelListener {

    public void onStageNumberChanged(int newStageNumber){}

    public void onStageTypeChanged(StageType stageType){}

    public void onCastleAttackedCountChanged(int newCastleAttackedCount){}

    public void onTimeFinished(){}

    public void onRemainingMiliSecsChanged(int remainingMiliSecs){}

    public void onFreezingMiliSecsChanged(int remainingMiliSecs){}

    public void onHintChanged(int newHintLeft){}

    public void onGameStateChanged(GameState oldState, GameState newState){}

    public void onCorrectClicked(SimpleRectangle rectangle, int remainingMiliSecsWhenClicked){}

    public void onAddedClickCount(String userId, int newCount){}

    public void onScoresChanged(double newScores){}

    public void onHandledAreasChanged() {}

}
