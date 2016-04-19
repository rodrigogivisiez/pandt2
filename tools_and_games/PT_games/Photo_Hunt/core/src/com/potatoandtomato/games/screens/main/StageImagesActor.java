package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.LightTimingModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.models.TimePeriodModel;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

/**
 * Created by SiongLeng on 19/4/2016.
 */
public class StageImagesActor {

    private Services services;
    private MyAssets assets;
    private GameCoordinator gameCoordinator;
    private Table imageOneTable;
    private Table imageTwoTable;
    private Table imageOneInnerTable;
    private Table imageTwoInnerTable;
    private Image blackBg, lightBulbOff, lightBulbBreak, lightBulbOn;

    public StageImagesActor(Services services, GameCoordinator gameCoordinator, Table imageOneTable, Table imageTwoTable, Table imageOneInnerTable, Table imageTwoInnerTable) {
        this.services = services;
        assets = services.getAssets();
        this.gameCoordinator = gameCoordinator;
        this.imageOneTable = imageOneTable;
        this.imageTwoTable = imageTwoTable;
        this.imageOneInnerTable = imageOneInnerTable;
        this.imageTwoInnerTable = imageTwoInnerTable;
    }

    public void reset(){
        imageTwoTable.clearActions();
        imageTwoTable.setPosition(0, 0);
        imageTwoInnerTable.setRotation(0);
        imageTwoInnerTable.setScale(1, 1);
        for(Actor actor : imageTwoTable.getChildren()){
            if(actor.getName() == null || !actor.getName().equals("innerTable")){
                actor.remove();
            }
        }
    }

    public void maneuver(BonusType bonusType, String extra, GameModel gameModel){
        switch (bonusType){
            case INVERTED:
                inverted();
                break;
            case LOOPING:
                looping();
                break;
            case ONE_PERSON:
                break;
            case LIGHTING:
                lighting(extra, gameModel);
                break;
            case MEMORY:

                break;
        }
    }

    public void inverted(){
        imageTwoInnerTable.setOrigin(Align.center);
        imageTwoInnerTable.setRotation(180);
    }

    public void lighting(String extra, GameModel gameModel){
        ObjectMapper objectMapper = new ObjectMapper();
        LightTimingModel lightTimingModel = null;
        try {
            lightTimingModel = objectMapper.readValue(extra, LightTimingModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        blackBg = new Image(assets.getTextures().get(Textures.Name.FULL_BLACK_BG));
        blackBg.setSize(imageTwoTable.getWidth(), imageTwoTable.getHeight());
        imageTwoTable.addActor(blackBg);

        lightBulbOff = new Image(assets.getTextures().get(Textures.Name.BULB_LIGHT_OFF));
        lightBulbOff.setSize(lightBulbOff.getPrefWidth(), lightBulbOff.getPrefHeight());
        lightBulbOff.setPosition(imageTwoTable.getWidth() - 25, imageTwoTable.getHeight() / 2 - lightBulbOff.getHeight() / 2);
        imageTwoTable.addActor(lightBulbOff);

        lightBulbBreak = new Image(assets.getTextures().get(Textures.Name.BULB_BROKEN));
        lightBulbBreak.setSize(lightBulbBreak.getPrefWidth(), lightBulbBreak.getPrefHeight());
        lightBulbBreak.setPosition(imageTwoTable.getWidth() - 25, imageTwoTable.getHeight() / 2 - lightBulbBreak.getHeight() / 2);
        lightBulbBreak.setVisible(false);
        imageTwoTable.addActor(lightBulbBreak);

        lightBulbOn = new Image(assets.getTextures().get(Textures.Name.BULB_LIGHT_ON));
        lightBulbOn.setSize(lightBulbOn.getPrefWidth(), lightBulbOn.getPrefHeight());
        lightBulbOn.setPosition(imageTwoTable.getWidth() - 76, imageTwoTable.getHeight() / 2 - lightBulbOn.getHeight() / 2);
        lightBulbOn.getColor().a = 0f;
        imageTwoTable.addActor(lightBulbOn);

        int totalTimingMiliSecs = gameModel.getThisStageTotalMiliSecs() + 10;

        if(lightTimingModel != null){
            int i = 0;
            for(TimePeriodModel periodModel : lightTimingModel.getTimePeriodModels()){

                int startMiliSecs = (int) ((periodModel.getStart() / 100) * totalTimingMiliSecs);
                int endMiliSecs = (int) ((periodModel.getEnd() / 100) * totalTimingMiliSecs);

                Threadings.delay(startMiliSecs, new Runnable() {
                    @Override
                    public void run() {
                        lightBulbOnAnimation();
                    }
                });

                final LightTimingModel finalLightTimingModel = lightTimingModel;
                final int finalI = i;
                Threadings.delay(endMiliSecs, new Runnable() {
                    @Override
                    public void run() {
                        if(finalI == finalLightTimingModel.getTimePeriodModels().size() - 1){
                            lightBulbBreakAnimation();
                        }
                        else{
                            lightBulbOffAnimation();
                        }

                    }
                });

                i++;
            }
        }

    }

    private void lightBulbOnAnimation(){
        lightBulbOn.clearActions();
        blackBg.clearActions();
        lightBulbOn.addAction(sequence(fadeIn(0.5f)));
        blackBg.addAction(sequence(fadeOut(0.5f)));
    }

    private void lightBulbOffAnimation(){
       // lightBulbOn.clearActions();
       // blackBg.clearActions();
        lightBulbOn.addAction(sequence(fadeOut(0.5f)));
        blackBg.addAction(sequence(fadeIn(0.5f)));
    }

    private void lightBulbBreakAnimation(){
        lightBulbOn.clearActions();
        blackBg.clearActions();
        blackBg.getColor().a = 1f;
        lightBulbOn.setVisible(false);
        lightBulbOff.setVisible(false);
        lightBulbBreak.setVisible(true);
    }

    public void looping(){
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

    public void memory(){
        Table blackBgTable = new Table();
        blackBgTable.setTransform(true);
        blackBgTable.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.FULL_BLACK_BG)));
        blackBgTable.setSize(imageTwoTable.getWidth(), imageTwoTable.getHeight());
        blackBgTable.setOrigin(Align.center);
        blackBgTable.setScale(0, 1);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR);
        Label startLabel = new Label(services.getTexts().memoryStart(), labelStyle);
        blackBgTable.add(startLabel);

        imageTwoTable.addActor(blackBgTable);

        blackBgTable.addAction(scaleTo(1, 1, 1.35f, Interpolation.exp10Out));

    }

    public void showDisallowClick(float x, float y, boolean isTableTwo){
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
    }








}
