package com.potatoandtomato.games.screens.stage_counter;

import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 13/4/2016.
 */
public class StageCounterLogic {

    private Services services;
    private GameCoordinator gameCoordinator;
    private StageCounterActor stageCounterActor;
    private GameModel gameModel;
    private StageType currentStageType;

    public StageCounterLogic(Services services, GameCoordinator gameCoordinator, GameModel gameModel) {
        this.services = services;
        this.gameCoordinator = gameCoordinator;
        this.gameModel = gameModel;

        stageCounterActor = new StageCounterActor(services);
        setListeners();
    }

    public void stageChanged(){
        stageCounterActor.refreshStageNumber(gameModel.getStageNumber(), gameModel.getStageType());
    }

    public void setListeners(){
        gameModel.addGameModelListener(new GameModelListener() {
            @Override
            public void onStageNumberChanged(int newStageNumber) {
                stageChanged();
            }

            @Override
            public void onStageTypeChanged(StageType stageType) {
                if(currentStageType != stageType){
                    stageChanged();
                }
            }
        });
    }


    public StageCounterActor getStageCounterActor() {
        return stageCounterActor;
    }
}
