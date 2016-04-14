package com.potatoandtomato.games.screens.time_bar;

import com.badlogic.gdx.math.Vector2;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.controls.Animator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.KnightState;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.statics.Global;

/**
 * Created by SiongLeng on 12/4/2016.
 */
public class KnightLogic {

    private GameModel gameModel;
    private Services services;
    private GameCoordinator gameCoordinator;
    private KnightActor knightActor;
    private float totalMiliSecs;
    private float totalMovingMiliSecs;
    private float totalAtkMiliSecs;
    private float totalDistance;
    private boolean freezed;

    public KnightLogic(GameModel gameModel, Services services, GameCoordinator gameCoordinator) {
        this.gameModel = gameModel;
        this.services = services;
        this.gameCoordinator = gameCoordinator;
        this.totalDistance = 450;

        this.knightActor = new KnightActor(services, totalDistance);
        reset();
        setListeners();
    }

    public void reset(){
        this.totalMiliSecs = gameModel.getThisStageTotalMiliSecs();
        this.totalMovingMiliSecs = gameModel.getThisStageTotalMovingMiliSecs();
        this.totalAtkMiliSecs = gameModel.getThisStageTotalAtkMiliSecs();
        knightActor.setKnightAtkSpeed(totalMiliSecs / (3000 * 1000));
        knightActor.changeState(KnightState.Walk);
        knightActor.setKnightPositionX(totalDistance, false);
    }

    public void updatePosition(float remainingMiliSecs){
        knightActor.setKnightPositionX(getRemainingDistanceByRemainingTime(remainingMiliSecs), true);
    }

    public void setFreezed(boolean freezed) {
        if(this.freezed != freezed){
            this.freezed = freezed;
            knightActor.setFreeze(freezed);
        }
    }

    public void setPause(boolean pause){
        if(pause){
            knightActor.stopAnimation();
        }
        else{
            knightActor.continueAnimation();
        }
    }

    public float getRemainingDistanceByRemainingTime(float remainingMiliSecs){
        float remainingDistance = 0;
        if(totalMiliSecs - remainingMiliSecs <= totalMovingMiliSecs){
            float percent =  (remainingMiliSecs - totalAtkMiliSecs) / totalMovingMiliSecs;
            remainingDistance = totalDistance * percent;
            if(remainingDistance < 0) remainingDistance = 0;
        }
        return remainingDistance;
    }

    public void setListeners(){
        gameModel.addGameModelListener(new GameModelListener() {

            @Override
            public void onStageNumberChanged(int newStageNumber) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        reset();
                    }
                });
            }

            @Override
            public void onRemainingMiliSecsChanged(final int remainingMiliSecs) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        updatePosition(remainingMiliSecs);
                    }
                });
            }

            @Override
            public void onFreezingMiliSecsChanged(final int remainingMiliSecs) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        setFreezed(remainingMiliSecs > 0);
                    }
                });

            }

            @Override
            public void onGameStateChanged(final GameState newState) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if(newState == GameState.Playing){
                            setPause(false);
                        }
                        else{
                            setPause(true);
                        }
                    }
                });
            }
        });

        knightActor.getKnightAtkAnimator().callBackOnIndex(Animator.IndexType.Last, new Runnable() {
            @Override
            public void run() {

            }
        });

    }

    public KnightActor getKnightActor() {
        return knightActor;
    }





}
