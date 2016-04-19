package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.absintf.StageImagesHandlerListener;
import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 18/4/2016.
 */
public class StageImagesLogic {

    private GameModel gameModel;
    private Table imageOneTable;
    private Table imageTwoTable;
    private Table imageOneInnerTable;
    private Table imageTwoInnerTable;
    private StageImagesActor stageImagesActor;
    private StageType stageType;
    private String extra;
    private GameCoordinator gameCoordinator;
    private Services services;
    private BonusType bonusType;
    private boolean disallowClick;
    private StageImagesHandlerListener stageImagesHandlerListener;

    public StageImagesLogic(GameCoordinator gameCoordinator, Services services, GameModel gameModel) {
        this.gameModel = gameModel;
        this.gameCoordinator = gameCoordinator;
        this.services = services;
    }

    public void init(Table imageOneTable, Table imageTwoTable, Table imageOneInnerTable, Table imageTwoInnerTable){
        this.imageOneTable = imageOneTable;
        this.imageTwoTable = imageTwoTable;
        this.imageOneInnerTable = imageOneInnerTable;
        this.imageTwoInnerTable = imageTwoInnerTable;
        stageImagesActor = new StageImagesActor(services, gameCoordinator,
                            imageOneTable, imageTwoTable, imageOneInnerTable, imageTwoInnerTable);
        setListeners();
    }

    public void beforeStartStage(StageType stageType, BonusType bonusType, String extra){
        this.stageType = stageType;
        this.extra = extra;
        this.bonusType = bonusType;
        this.disallowClick = false;
        process();
    }

    public void process(){
        stageImagesActor.reset();

        if(bonusType == BonusType.MEMORY){
            disallowClick = true;
            int startTime = gameModel.getThisStageTotalMiliSecs() * 25 / 100;
            int showCircleTime = gameModel.getThisStageTotalMiliSecs() * 15 / 100;

            Threadings.delay(showCircleTime, new Runnable() {
                @Override
                public void run() {
                    stageImagesHandlerListener.requestCircleAll();
                }
            });

            Threadings.delay(startTime, new Runnable() {
                @Override
                public void run() {
                    stageImagesHandlerListener.cancelCircleAll();
                    stageImagesActor.memory();
                    disallowClick = false;
                }
            });

        }

        stageImagesActor.maneuver(bonusType, extra, gameModel);
    }

    public boolean checkCanTouch(float x, float y, boolean isTableTwo){
        boolean canTouch = true;

        if(disallowClick){
            canTouch = false;
        }
        else if(this.bonusType == BonusType.ONE_PERSON && !extra.equals(gameCoordinator.getMyUserId())){
            canTouch = false;
        }


        if(!canTouch){
            stageImagesActor.showDisallowClick(x, y, isTableTwo);
        }

        return canTouch;
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

    }

    public void setStageImagesHandlerListener(StageImagesHandlerListener stageImagesHandlerListener) {
        this.stageImagesHandlerListener = stageImagesHandlerListener;
    }
}
