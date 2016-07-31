package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.PapyrusType;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.screens.papyruses.BeforeBonusPapyrusScene;
import com.potatoandtomato.games.screens.papyruses.BeforeStartPapyrusScene;
import com.potatoandtomato.games.screens.papyruses.GameOverPapyrusScene;
import com.potatoandtomato.games.screens.papyruses.PapyrusSceneAbstract;

/**
 * Created by SiongLeng on 23/5/2016.
 */
public class StageStateLogic implements Disposable {

    private GameModel gameModel;
    private Services services;
    private GameCoordinator gameCoordinator;
    private StageStateActor stageStateActor;
    private PapyrusSceneAbstract currentPapyrusScene;
    private GameState previousState;
    private boolean papyrusOpened;
    private int currentIndex;

    public StageStateLogic(GameModel gameModel, Services services, GameCoordinator gameCoordinator) {
        this.gameModel = gameModel;
        this.services = services;
        this.gameCoordinator = gameCoordinator;

        stageStateActor = new StageStateActor(services);
        setListeners();

    }

    public void stateChanged(final GameState newState){
        if(previousState != null && previousState == newState){
            return;
        }
        currentIndex++;

        if(newState == GameState.BeforeNewGame){
            openPapyrus(PapyrusType.BeforeNewGame);
            previousState = newState;
        }
        else if(newState == GameState.BeforeContinue){
            closeCurrentPapyrus();
            previousState = newState;
        }
        else if(newState == GameState.BeforeBouns){
            openPapyrus(PapyrusType.Bonus);
            previousState = newState;
        }
        else if(newState == GameState.Lose && gameModel.getStageType() != StageType.Bonus){
            Threadings.delay(5000, new Runnable() {         //delay to show all correct answers before close
                @Override
                public void run() {
                    openPapyrus(PapyrusType.GameOver);
                }
            });
            previousState = newState;
        }
        else if(newState == GameState.PrePlaying){
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    if(previousState == GameState.BeforeNewGame){
                        if(currentPapyrusScene instanceof BeforeStartPapyrusScene){
                            ((BeforeStartPapyrusScene) currentPapyrusScene).gameReadyToStart();
                        }
                    }
                    else if(previousState == GameState.Lose){
                        closeCurrentPapyrus();
                    }

                    previousState = newState;
                }
            });
        }
        else if(newState == GameState.Playing){
            if(isPapyrusOpened()){
                closeCurrentPapyrus();
            }
        }

//        else if(newState == GameState.Won){
//            if(previousState == GameState.BeforeContinue){
//                if(currentPapyrusScene instanceof BeforeStartPapyrusScene){
//                    ((BeforeStartPapyrusScene) currentPapyrusScene).gameReadyToStart();
//                }
//            }
//            previousState = newState;
//        }
    }

    public void openPapyrus(final PapyrusType papyrusType){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                PapyrusSceneAbstract papyrusScene = papyrusTypeToScene(papyrusType);
                papyrusScene.setOnClosingRunnable(new Runnable() {
                    @Override
                    public void run() {
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                closeCurrentPapyrus();
                            }
                        });
                    }
                });
                if(currentPapyrusScene != null){
                    stageStateActor.switchPapyrus(papyrusScene);
                }
                else{
                    stageStateActor.openPapyrus(papyrusScene);
                }
                currentPapyrusScene = papyrusScene;
                papyrusOpened = true;
            }
        });
    }

    public void closeCurrentPapyrus(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                papyrusOpened = false;
                stageStateActor.closePapyrus(new Runnable() {
                    @Override
                    public void run() {
                        if(currentPapyrusScene != null) currentPapyrusScene.dispose();
                        currentPapyrusScene = null;
                    }
                });
            }
        });

    }

    public PapyrusSceneAbstract papyrusTypeToScene(PapyrusType papyrusType){
        switch (papyrusType){
            case BeforeContinue:
                return new BeforeStartPapyrusScene(services, stageStateActor.getPapyrusContentTable(), true);
            case BeforeNewGame:
                return new BeforeStartPapyrusScene(services, stageStateActor.getPapyrusContentTable(), false);
            case Bonus:
                return new BeforeBonusPapyrusScene(services, stageStateActor.getPapyrusContentTable());
            case GameOver:
                return new GameOverPapyrusScene(services, stageStateActor.getPapyrusContentTable(), gameModel, gameCoordinator);
        }
        return null;
    }

    public void setBonusMeta(final BonusType bonusType, final String extra){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(currentPapyrusScene instanceof BeforeBonusPapyrusScene){
                    ((BeforeBonusPapyrusScene) currentPapyrusScene).revealBonus(bonusType, extra);
                }
            }
        });

    }

    public void setListeners(){
        gameModel.addGameModelListener(new GameModelListener() {
            @Override
            public void onGameStateChanged(GameState oldState, GameState newState) {
                stateChanged(newState);
            }
        });
    }

    public boolean isPapyrusOpened(){
        return papyrusOpened;
    }

    public StageStateActor getStageStateActor() {
        return stageStateActor;
    }

    @Override
    public void dispose() {
        if(currentPapyrusScene != null) currentPapyrusScene.dispose();
    }
}
