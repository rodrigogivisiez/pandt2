package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.absintf.StageImagesHandlerListener;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.controls.Cross;
import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 18/4/2016.
 */
public class StageImagesHandler {

    private GameModel gameModel;
    private Table imageOneTable;
    private Table imageTwoTable;
    private Table imageOneInnerTable;
    private Table imageTwoInnerTable;
    private StageType stageType;
    private String extra;
    private GameCoordinator gameCoordinator;
    private Services services;
    private BonusType bonusType;
    private StageImagesHandlerListener stageImagesHandlerListener;

    public StageImagesHandler(GameCoordinator gameCoordinator, Services services, GameModel gameModel) {
        this.gameModel = gameModel;
        this.gameCoordinator = gameCoordinator;
        this.services = services;
    }

    public void init(Table imageOneTable, Table imageTwoTable, Table imageOneInnerTable, Table imageTwoInnerTable){
        this.imageOneTable = imageOneTable;
        this.imageTwoTable = imageTwoTable;
        this.imageOneInnerTable = imageOneInnerTable;
        this.imageTwoInnerTable = imageTwoInnerTable;
        setListeners();
    }

    public void beforeStartStage(StageType stageType, BonusType bonusType, String extra){
        this.stageType = stageType;
        this.extra = extra;
        this.bonusType = bonusType;
        reset();
        process();
    }

    private void reset(){
        imageTwoInnerTable.setRotation(0);
        for(Actor actor : imageTwoTable.getChildren()){
            if(actor.getName() != null && actor.getName().equals("duplicate")){
                actor.remove();
            }
        }
    }

    public void process(){
        if(this.bonusType == BonusType.INVERTED){
            imageTwoInnerTable.setOrigin(Align.center);
            imageTwoInnerTable.setRotation(180);
        }
        else if(this.bonusType == BonusType.LOOPING){
            imageTwoTable.setTransform(true);
            imageTwoTable.addAction(forever(moveBy(-imageTwoInnerTable.getWidth(), 0, 5f)));

            Image image = imageTwoInnerTable.findActor("image");

            if(image != null){
                for(int i = 1; i < 20; i++){
                    Table imageTwoDuplicateInnerTable = new Table();
                    Image imageTwoImage = new Image(image.getDrawable());
                    imageTwoDuplicateInnerTable.add(imageTwoImage).expand().fill();
                    imageTwoDuplicateInnerTable.setSize(imageTwoInnerTable.getWidth(), imageTwoInnerTable.getHeight());
                    imageTwoDuplicateInnerTable.setPosition(imageTwoInnerTable.getWidth() * i, 0);
                    imageTwoDuplicateInnerTable.setName("duplicate");
                    imageTwoTable.addActor(imageTwoDuplicateInnerTable);
                }
            }
        }
    }

    public boolean checkCanTouch(float x, float y, boolean isTableTwo){
        if(this.bonusType == BonusType.ONE_PERSON && !extra.equals(gameCoordinator.getMyUserId())){
            Table touchTable = isTableTwo ? imageTwoInnerTable : imageOneInnerTable;
            final Image stopImage = new Image(services.getAssets().getTextures().get(Textures.Name.STOP_ICON));
            stopImage.setPosition(x - stopImage.getPrefWidth() / 2, y  - stopImage.getPrefHeight() / 2);
            stopImage.setSize(stopImage.getPrefWidth(), stopImage.getPrefHeight());
            touchTable.addActor(stopImage);

            stopImage.addAction(sequence(delay(0.6f), fadeOut(0.3f), new RunnableAction(){
                @Override
                public void run() {
                    stopImage.remove();
                }
            }));

            return false;
        }
        return true;
    }

    public Vector2 processTouch(float x, float y, boolean isTableTwo){
        Vector2 result = new Vector2(x, y);
        if(this.bonusType == BonusType.INVERTED && isTableTwo){
            result.x = Math.abs(x - imageTwoTable.getWidth());
            result.y = Math.abs(y - imageTwoTable.getHeight());
        }
        else if(this.bonusType == BonusType.LOOPING && isTableTwo){
            int count = (int) Math.floor(x / imageTwoTable.getWidth());
            result.x = x - count *imageTwoTable.getWidth();
        }
        return result;
    }

    public void setListeners(){
        imageOneTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(checkCanTouch(x, y, false)){
                    Vector2 result = processTouch(x, y, false);
                    stageImagesHandlerListener.onTouch(result.x, result.y);
                }

            }
        });

        imageTwoTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (checkCanTouch(x, y, true)) {
                    Vector2 result = processTouch(x, y, true);
                    stageImagesHandlerListener.onTouch(result.x,result.y);
                }
            }
         });

        gameModel.addGameModelListener(new GameModelListener() {
            @Override
            public void onGameStateChanged(GameState newState) {
                if (newState == GameState.Won) {
                    Threadings.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            imageTwoTable.clearActions();
                            imageTwoTable.setPosition(0, 0);
                        }
                    });
                }
            }
        });

    }

    public void setStageImagesHandlerListener(StageImagesHandlerListener stageImagesHandlerListener) {
        this.stageImagesHandlerListener = stageImagesHandlerListener;
    }
}
