package com.potatoandtomato.games.screens.hints;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.absintf.HintsLogicListener;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.models.SimpleRectangle;
import com.potatoandtomato.games.statics.Global;

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
            currentHintsLeft = gameModel.getHintsLeft();
            hintsActor.refreshDesign(currentHintsLeft);
        }
    }


    private void setListeners(){
        this.hintsActor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x1, float y1) {
                if(gameModel.isPlaying() && gameModel.getHintsLeft() > 0){
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
                        gameModel.addHandledArea(new SimpleRectangle(notYetHandledArea), gameCoordinator.getMyUserId(),
                                gameModel.getRemainingMiliSecs());
                        float x, y;
                        x = notYetHandledArea.getX() + 1;
                        y = notYetHandledArea.getY() + 1;
                        y = gameModel.getImageDetails().getGameImageHeight() - y;
                        services.getRoomMsgHandler().sendTouched(x, y,
                                                true, gameModel.getRemainingMiliSecs());
                        if(!Global.REVIEW_MODE){
                            gameModel.minusHintLeft();
                        }
                    }
                }
            }
        });

        gameModel.addGameModelListener(new GameModelListener() {
            @Override
            public void onHintChanged(int newHintLeft) {
                invalidate();
            }
        });
    }

    public HintsActor getHintsActor() {
        return hintsActor;
    }

    public void setHintsLogicListener(HintsLogicListener hintsLogicListener) {
        this.hintsLogicListener = hintsLogicListener;
    }




}
