package com.potatoandtomato.games.screens.time_bar;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.enums.CastleState;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.statics.Global;

/**
 * Created by SiongLeng on 12/4/2016.
 */
public class CastleLogic {

    private GameModel gameModel;
    private Services services;
    private GameCoordinator gameCoordinator;
    private CastleActor castleActor;
    private int totalMiliSecs;
    private int totalAtkMiliSecs;
    private CastleState previousState;

    public CastleLogic(GameModel gameModel, Services services, GameCoordinator gameCoordinator) {
        this.services = services;
        this.gameModel = gameModel;
        this.gameCoordinator = gameCoordinator;

        this.castleActor = new CastleActor(services);
        this.castleActor.changeState(CastleState.Normal);
        setListeners();
    }

    public void reset(){
        this.totalMiliSecs = gameModel.getThisStageTotalMiliSecs();
        this.totalAtkMiliSecs = gameModel.getThisStageTotalAtkMiliSecs();
        updateRemainingMiliSecs(totalMiliSecs);
    }

    public void updateRemainingMiliSecs(int remainingMiliSecs){
        CastleState currentState = getCastleState(remainingMiliSecs);
        if(previousState != currentState){
            Logs.show("Castle state: " + currentState);
            this.castleActor.changeState(currentState);
            previousState = currentState;
        }
    }

    public CastleState getCastleState(int remainingMiliSecs){
        if(remainingMiliSecs < (totalAtkMiliSecs * 0.3) && remainingMiliSecs > 0){
            return CastleState.Semi_Destroyed;
        }
        else if(remainingMiliSecs <= 0){
            return CastleState.Destroyed;
        }
        else{
            return CastleState.Normal;
        }
    }

    public void setListeners(){
        gameModel.addGameModelListener(new GameModelListener() {

            @Override
            public void onCastleAttackedCountChanged(int newCastleAttackedCount) {
                updateRemainingMiliSecs(gameModel.getRemainingMiliSecs());
            }

            @Override
            public void onStageNumberChanged(int newStageNumber) {
                reset();
            }

            @Override
            public void onGameStateChanged(GameState oldState, GameState newState) {
                if(newState == GameState.Won || newState == GameState.Lose){
                    updateRemainingMiliSecs(gameModel.getRemainingMiliSecs());
                }
            }
        });
    }


    public CastleActor getCastleActor() {
        return castleActor;
    }
}
