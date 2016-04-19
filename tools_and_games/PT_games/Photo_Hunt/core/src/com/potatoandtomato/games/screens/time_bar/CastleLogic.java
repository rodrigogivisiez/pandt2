package com.potatoandtomato.games.screens.time_bar;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.enums.CastleState;
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
        this.castleActor.changeState(getCastleState(remainingMiliSecs));
    }

    public CastleState getCastleState(int remainingMiliSecs){
        if(remainingMiliSecs < (totalAtkMiliSecs * 0.25)){
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
            public void onRemainingMiliSecsChanged(final int remainingMiliSecs) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        updateRemainingMiliSecs(remainingMiliSecs);
                    }
                });
            }

            @Override
            public void onStageNumberChanged(int newStageNumber) {
                reset();
            }
        });
    }


    public CastleActor getCastleActor() {
        return castleActor;
    }
}
