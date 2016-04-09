package com.potatoandtomato.games.screens.hints;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoandtomato.games.absintf.HintsLogicListener;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 8/4/2016.
 */
public class HintsLogic {

    private HintsActor hintsActor;
    private GameModel gameModel;
    private int currentHintsLeft;
    private HintsLogicListener hintsLogicListener;
    private Services services;

    public HintsLogic(GameModel gameModel, Services services) {
        this.gameModel = gameModel;
        this.services = services;
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
            public void clicked(InputEvent event, float x, float y) {
                if(hintsLogicListener != null && gameModel.getHintsLeft() > 0){
                    hintsLogicListener.onHintClicked();
                }
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
