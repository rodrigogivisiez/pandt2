package com.potatoandtomato.games.screens.hints;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.absintf.HintsLogicListener;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 8/4/2016.
 */
public class HintsLogic {

    private HintsActor hintsActor;
    private GameModel gameModel;
    private int currentHintsLeft;
    private GameCoordinator gameCoordinator;
    private HintsLogicListener hintsLogicListener;
    private Services services;
    private boolean isHintBlocked;
    private int usedCount = 1;

    public HintsLogic(GameModel gameModel, Services services, GameCoordinator gameCoordinator) {
        this.gameModel = gameModel;
        this.services = services;
        this.gameCoordinator = gameCoordinator;
        this.hintsActor = new HintsActor(services);
        setListeners();
        invalidate();
    }

    public void invalidate(){
        if(currentHintsLeft != gameModel.getHintsLeft()){
            if(currentHintsLeft == gameModel.getHintsLeft() + 1){
                services.getSoundsWrapper().playSounds(Sounds.Name.HINT);
            }

            currentHintsLeft = gameModel.getHintsLeft();
            hintsActor.refreshDesign(currentHintsLeft);
        }
    }

    public void refreshBlockHint(){
        boolean blockHint = (gameModel.getStageType() == StageType.Bonus);
        if(isHintBlocked != blockHint){
            isHintBlocked = blockHint;
            hintsActor.setHintBlockVisible(isHintBlocked);
        }
    }

    public void reviveAllHints(){
        gameModel.setHintsLeft(3);
    }

    private void setListeners(){
        this.hintsActor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x1, float y1) {
//                if(usedCount > 3){      //hotfix for fast consecutive click can use more than 3 hints bug
//                    return;
//                }
//                usedCount++;

                if(gameModel.isPlaying() && gameModel.getHintsLeft() > 0 && !isHintBlocked){
                    int i = 0;
                    Rectangle notYetHandledArea = null;
                    for(Rectangle rectangle : gameModel.getImageDetails().getCorrectRects()){
                        if(!gameModel.isAreaAlreadyHandled(rectangle)){
                            notYetHandledArea = rectangle;
                            break;
                        }
                        i++;
                    }

                    if(notYetHandledArea != null){
                        gameModel.minusHintLeft();
                        hintsLogicListener.onHintClicked(notYetHandledArea, gameModel.getHintsLeft());
                    }
                }

            }
        });

        gameModel.addGameModelListener(new GameModelListener() {

            @Override
            public void onGameStateChanged(GameState newState) {
                if(newState == GameState.WaitingForNextStage && gameModel.getStageType() == StageType.Bonus){
                    reviveAllHints();
                }
            }

            @Override
            public void onHintChanged(int newHintLeft) {
                invalidate();
            }

            @Override
            public void onStageNumberChanged(int newStageNumber) {
                usedCount = 1;
                refreshBlockHint();
            }
        });
    }

    public int getCurrentHintsLeft() {
        if(isHintBlocked){
            return 0;
        }
        else{
            return gameModel.getHintsLeft();
        }
    }

    public HintsActor getHintsActor() {
        return hintsActor;
    }

    public void setHintsLogicListener(HintsLogicListener hintsLogicListener) {
        this.hintsLogicListener = hintsLogicListener;
    }




}
