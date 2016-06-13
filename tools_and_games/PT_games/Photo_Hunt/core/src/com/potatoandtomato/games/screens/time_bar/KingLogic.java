package com.potatoandtomato.games.screens.time_bar;

import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.KingState;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.statics.Global;

/**
 * Created by SiongLeng on 12/4/2016.
 */
public class KingLogic{

    private GameModel gameModel;
    private Services services;
    private KingActor kingActor;
    private int totalMiliSecs;
    private int totalAtkMiliSecs;

    public KingLogic(GameModel gameModel, Services services) {
        this.services = services;
        this.gameModel = gameModel;

        this.kingActor = new KingActor(services);
        setListeners();

    }

    public void reset(){
        this.totalMiliSecs = gameModel.getThisStageTotalMiliSecs();
        this.totalAtkMiliSecs = gameModel.getThisStageTotalAtkMiliSecs();
        updateRemainingMiliSecs(totalMiliSecs);
    }

    public void updateRemainingMiliSecs(int remainingMiliSecs){
        if(remainingMiliSecs <= (totalAtkMiliSecs * 0.3)){
            kingActor.changeState(KingState.Panic);
        }
        else{
            kingActor.changeState(KingState.Normal);
        }
    }

    public void updateKingByGameState(GameState gameState){
        if(gameState == GameState.Won || gameState == GameState.WonWithoutContributions){
            kingActor.changeState(KingState.Win);
        }
        else if(gameState == GameState.Lose){
            kingActor.changeState(KingState.Lose);
        }
    }

    public void setPaused(boolean pause){
        if(pause){
            kingActor.stopAnimation();
        }
        else{
            kingActor.continueAnimation();
        }
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
                        updateRemainingMiliSecs(remainingMiliSecs);
                    }
                });
            }

            @Override
            public void onGameStateChanged(GameState oldState, final GameState newState) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (newState == GameState.Playing) {
                            setPaused(false);
                        }
                        else if(newState == GameState.Won || newState == GameState.WonWithoutContributions){
                            setPaused(false);
                            updateKingByGameState(newState);
                        }
                        else if(newState == GameState.Lose){
                            setPaused(true);
                            Threadings.delay(2500, new Runnable() {
                                @Override
                                public void run() {
                                    updateKingByGameState(newState);
                                    setPaused(false);
                                }
                            });
                        }
                        else if(newState == GameState.BeforeNewGame){
                            setPaused(true);
                        }
                    }
                });
            }
        });
    }

    public KingActor getKingActor() {
        return kingActor;
    }

}
