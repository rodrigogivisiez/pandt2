package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.helpers.NotifyRunnable;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.LightTimingModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.models.TimePeriodModel;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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
    private SafeThread safeThread;

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
        imageTwoTable.setClip(true);
        imageTwoInnerTable.setRotation(0);
        imageTwoInnerTable.setScale(1, 1);
        imageTwoInnerTable.setVisible(true);


        if(safeThread != null) safeThread.kill();

        resetAllTable();
    }

    public void stop(){
        if(safeThread != null) safeThread.kill();
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
            case TORCH_LIGHT:
                torchLight();
                break;
            case DISTRACTION:
                distractions(extra);
                break;
            case WRINKLE:
                wrinkle();
                break;
            case COVERED:
                covered();
                break;
            case EGG:
                egg();
                break;
        }
    }

    public void egg(){
        int colCount = 10;
        int rowCount = 8;
        final float eachColWidth = (int) (imageTwoTable.getWidth() / colCount);
        final float eachRowHeight = (int) (imageTwoTable.getHeight() / rowCount);

        final ArrayList<Vector2> randoms = new ArrayList();

        for(int i = 0; i < colCount; i++){
            for(int q = 0; q < rowCount; q++){
                randoms.add(new Vector2(i, q));
            }
        }

        safeThread = new SafeThread();

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int i =0;
                while (true){
                    if(safeThread.isKilled()) return;

                    final int finalI = i;
                    Threadings.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            int min = 5;
                            int max = 10;
                            if(finalI > 5){
                                min = 40;
                                max = 50;
                            }
                            else if(finalI > 3){
                                min = 20;
                                max = 30;
                            }
                            putEggs(randoms, eachColWidth, eachRowHeight, MathUtils.random(min, max));
                        }
                    });

                    Threadings.sleep(MathUtils.random(1000, 3000));
                    i++;
                }
            }
        });

    }

    private void putEggs(ArrayList<Vector2> randoms, final float eachColWidth , float eachRowHeight, int totalCount){

        for(int i = imageOneTable.getChildren().size - 1 ; i >=0; i--){
            Actor actor = imageOneTable.getChildren().get(i);
            if(actor.getName() != null && actor.getName().equals("egg")){
                actor.clearActions();
                actor.remove();
            }
        }

        for(int i = imageTwoTable.getChildren().size - 1 ; i >=0; i--){
            Actor actor = imageTwoTable.getChildren().get(i);
            if(actor.getName() != null && actor.getName().equals("egg")){
                actor.clearActions();
                actor.remove();
            }
        }

        Collections.shuffle(randoms);
        for(int i = 0; i < totalCount; i++){
            final Image eggImage = new Image(assets.getTextures().get(Textures.Name.EGG));
            eggImage.setName("egg");
            eggImage.setOrigin(Align.center);
            Vector2 position = randoms.get(i);
            eggImage.setPosition(position.x * eachColWidth + eggImage.getWidth() / 4, position.y * eachRowHeight);
            eggImage.addAction(forever(sequence(rotateBy(-30, 0.5f),rotateBy(30, 0.5f))));
            imageOneTable.addActor(eggImage);
            eggImage.addListener(new ClickListener(){

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                   event.stop();
                    eggToYolk(eggImage, imageOneTable);
                    return false;
                }

            });
        }

        Collections.shuffle(randoms);
        for(int i = 0; i < totalCount; i++){
            Vector2 position = randoms.get(i);
            final Image eggImage2 = new Image(assets.getTextures().get(Textures.Name.EGG));
            eggImage2.setName("egg");
            eggImage2.setOrigin(Align.center);
            eggImage2.setPosition(position.x * eachColWidth + eggImage2.getWidth() / 4, position.y * eachRowHeight);
            eggImage2.addAction(forever(sequence(rotateBy(-30, 0.5f),rotateBy(30, 0.5f))));
            imageTwoTable.addActor(eggImage2);

            eggImage2.addListener(new ClickListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    event.stop();
                    eggToYolk(eggImage2, imageTwoTable);
                    return false;
                }
            });
        }



    }

    private void eggToYolk(Actor egg, Table table){
        Image yolkImage = new Image(assets.getTextures().get(Textures.Name.YOLK));
        Vector2 position = new Vector2(egg.getX() + egg.getWidth() / 2, egg.getY() + egg.getHeight() / 2);
        yolkImage.setPosition(position.x - yolkImage.getPrefWidth() / 2, position.y - yolkImage.getPrefHeight() / 2);
        egg.clearActions();
        egg.remove();

        yolkImage.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        table.addActor(yolkImage);
    }

    public void covered(){
        Image image = (Image) imageTwoInnerTable.findActor("image");
        imageTwoInnerTable.setVisible(false);

        Table fakeInnerTable = new Table();
        fakeInnerTable.align(Align.topLeft);
        imageTwoTable.add(fakeInnerTable).expand().fill();


        for(int row = 1; row >= 0; row--){
            for(int col = 0; col < 3; col++){
                Table innerTable = new Table();
                innerTable.setFillParent(true);
                Image image1 = new Image(image.getDrawable());
                image1.setSize(imageTwoInnerTable.getWidth(), imageTwoInnerTable.getHeight());
                image1.setPosition(col * (-imageTwoInnerTable.getWidth() / 3),
                                        row * (-image1.getHeight() + imageTwoInnerTable.getHeight() / 2));
                innerTable.setClip(true);
                innerTable.addActor(image1);

                final Image coverImage = new Image(assets.getTextures().get(Textures.Name.COVERED));
                coverImage.setFillParent(true);

                Table table1 = new Table();
                table1.addActor(innerTable);
                table1.addActor(coverImage);
                fakeInnerTable.add(table1).size(imageTwoInnerTable.getWidth() / 3, imageTwoInnerTable.getHeight() / 2).space(1.5f);

                coverImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        float originalAlpha = coverImage.getColor().a;
                        originalAlpha -= 0.25f;
                        if(originalAlpha < 0) originalAlpha = 0f;
                        coverImage.getColor().a = originalAlpha;

                        coverImage.clearActions();
                        coverImage.addAction(sequence(delay(4f), fadeIn(1f)));
                    }
                });

            }
            fakeInnerTable.row();
        }
    }

    public void wrinkle(){
        Image wrinkleImage = new Image(assets.getTextures().get(Textures.Name.WRINKLE_BG));
        wrinkleImage.getColor().a = 0.6f;
        imageTwoTable.addActor(wrinkleImage);
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
        imageTwoTable.setClip(false);

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

    public void torchLight(){
        Image vignetteImage = new Image(assets.getTextures().get(Textures.Name.VIGNETTE));
        vignetteImage.setSize(vignetteImage.getPrefWidth(), vignetteImage.getPrefHeight());
        vignetteImage.setPosition(-235 , -280);
        imageTwoTable.addActor(vignetteImage);


        vignetteImage.addAction(forever(sequence(moveTo(-375, -180, 2f),  moveTo(-95, -180, 4f),
                                            moveTo(-375, -370, 4f), moveTo(-95, -280, 3f),
                                            moveTo(-375, -280, 4f), moveTo(-375, -180, 2f),
                                            moveTo(-375, -370, 4f),  moveTo(-95, -370, 4f),
                                            moveTo(-95, -180, 4f), moveTo(-235, -280, 2f))));

    }

    public void distractions(String extra){
        final ArrayList<NotifyRunnable> runnables = new ArrayList();

        final NotifyRunnable run1 = new NotifyRunnable() {
            @Override
            public void run() {
                final NotifyRunnable _this = this;
                final Image monstersImage = new Image(assets.getTextures().get(Textures.Name.MONSTERS));
                final Image monstersInvertImage = new Image(assets.getTextures().get(Textures.Name.MONSTERS_INVERT));
                monstersImage.setPosition(imageTwoTable.getWidth() / 2 - monstersImage.getPrefWidth() / 2, -100);
                monstersInvertImage.setPosition(imageTwoTable.getWidth() / 2 - monstersInvertImage.getPrefWidth() / 2, 270);

                imageTwoTable.addActor(monstersImage);
                imageTwoTable.addActor(monstersInvertImage);

                monstersImage.addAction(sequence(moveTo(monstersImage.getX(), -30, 2f), delay(4f), moveTo(monstersImage.getX(), -100, 2f)));
                monstersInvertImage.addAction(sequence(moveTo(monstersInvertImage.getX(), 200, 2f), delay(4f), moveTo(monstersImage.getX(), 270, 2f), new RunnableAction(){
                    @Override
                    public void run() {
                        _this.setFinish(true);
                    }
                }));
            }
        };

        runnables.add(run1);

        //////////////////////////////////////////////////////////////

        NotifyRunnable run2 = new NotifyRunnable() {
            @Override
            public void run() {
                final NotifyRunnable _this = this;
                final Image giraffeImage = new Image(assets.getTextures().get(Textures.Name.GIRAFFE));
                giraffeImage.setPosition(imageTwoTable.getWidth() + 30, -140);

                imageTwoTable.addActor(giraffeImage);

                giraffeImage.addAction(sequence(moveTo(imageTwoTable.getWidth() - 30, giraffeImage.getY(), 5f),
                                                delay(3f),
                                                moveTo(imageTwoTable.getWidth() /2 - 100, giraffeImage.getY(), 3f),
                                                delay(8f),
                                                moveTo(-180, giraffeImage.getY(), 4f), new RunnableAction(){
                            @Override
                            public void run() {
                                _this.setFinish(true);
                            }
                        }));
            }
        };

        runnables.add(run2);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        NotifyRunnable run3 = new NotifyRunnable() {
            @Override
            public void run() {
                final NotifyRunnable _this = this;
                final boolean[] stop = {false};

                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            if(safeThread.isKilled()){
                                stop[0] = true;
                                break;
                            }
                        }
                    }
                });

                if(stop[0]) return;
                moveCat(0);

                Threadings.delay(6000, new Runnable() {
                    @Override
                    public void run() {
                        if(stop[0]) return;
                        moveCat(-30);
                        moveCat(30);
                        Threadings.delay(6000, new Runnable() {
                            @Override
                            public void run() {
                                for(int i = 0; i < 30; i++){
                                    if(stop[0]) return;
                                    Threadings.delay(i * 300, new Runnable() {
                                        @Override
                                        public void run() {
                                            moveCat(-90);
                                            moveCat(-70);
                                            moveCat(-40);
                                            moveCat(-10);
                                            moveCat(20);
                                            moveCat(40);
                                            moveCat(60);
                                        }
                                    });
                                }
                                Threadings.delay(300 * 31, new Runnable() {
                                    @Override
                                    public void run() {
                                        _this.setFinish(true);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        };

        runnables.add(run3);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        NotifyRunnable run4 = new NotifyRunnable() {
            @Override
            public void run() {
                final NotifyRunnable _this = this;
                final Image vampireImage = new Image(assets.getTextures().get(Textures.Name.VAMPIRE));
                vampireImage.setPosition(-100, -100);

                imageTwoTable.addActor(vampireImage);

                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        for(int i =0; i< 20; i++){
                            final boolean[] finish = new boolean[1];
                            Threadings.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    moveToRandomPosition(vampireImage, new Runnable() {
                                        @Override
                                        public void run() {
                                            finish[0] = true;
                                        }
                                    });
                                }
                            });
                            while (!finish[0]){
                                Threadings.sleep(300);
                            }
                        }
                        vampireImage.addAction(sequence(moveTo(-100, -100, 3f), new RunnableAction(){
                            @Override
                            public void run() {
                                _this.setFinish(true);
                            }
                        }));
                    }
                });
            }
        };

        runnables.add(run4);

        /////////////////////////////////////////////////////////////////////////////////////////////////////

        NotifyRunnable run5 = new NotifyRunnable() {
            @Override
            public void run() {
                final NotifyRunnable _this = this;
                final Image vanImage = new Image(assets.getTextures().get(Textures.Name.VAN));
                vanImage.setPosition(-300, 150);

                imageTwoTable.addActor(vanImage);

                vanImage.addAction(sequence(moveBy(400, 0, 8f), delay(3f), moveBy(200, 0, 3f), delay(1f), moveBy(-200, 0, 4f),
                                        delay(4f), moveBy(400, 0, 2f), new RunnableAction(){
                            @Override
                            public void run() {
                                _this.setFinish(true);
                            }
                        }));
            }
        };

        runnables.add(run5);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        NotifyRunnable run6 = new NotifyRunnable() {
            @Override
            public void run() {
                final NotifyRunnable _this = this;
                final Image birthdayImage = new Image(assets.getTextures().get(Textures.Name.BIRTHDAY));
                birthdayImage.setOrigin(Align.center);
                birthdayImage.setPosition(imageTwoTable.getWidth() / 2 - birthdayImage.getPrefWidth() / 2,
                        imageTwoTable.getHeight() / 2 - birthdayImage.getPrefHeight() / 2);
                birthdayImage.setScale(0);

                imageTwoTable.addActor(birthdayImage);

                birthdayImage.addAction(sequence(scaleTo(1.5f, 1.5f, 2f, Interpolation.bounceOut), delay(5f), scaleTo(0, 0, 1f), new RunnableAction(){
                    @Override
                    public void run() {
                        _this.setFinish(true);
                    }
                }));
            }
        };

        runnables.add(run6);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////

        NotifyRunnable run7 = new NotifyRunnable() {
            @Override
            public void run() {
                final NotifyRunnable _this = this;
                final Image santaLeftImage = new Image(assets.getTextures().get(Textures.Name.SANTA_LEFT));
                final Image deerRightImage = new Image(assets.getTextures().get(Textures.Name.DEER_RIGHT));
                santaLeftImage.setPosition(-100, -100);
                deerRightImage.setPosition(imageTwoTable.getWidth() + 100, -100);

                imageTwoTable.addActor(santaLeftImage);
                imageTwoTable.addActor(deerRightImage);

                santaLeftImage.addAction(sequence(moveBy(100, 100, 3f), delay(5f), moveBy(-100, -100, 3f)));
                deerRightImage.addAction(sequence(moveBy(-200, 100, 3f), delay(5f), moveBy(100, -100, 3f), new RunnableAction(){
                    @Override
                    public void run() {
                        _this.setFinish(true);
                    }
                }));
            }
        };

        runnables.add(run7);
        //run7.run();

        /////////////////////////////////////////////////////////////////////////////////////////////////////////

        NotifyRunnable run8 = new NotifyRunnable() {
            @Override
            public void run() {
                final NotifyRunnable _this = this;
                final Image sexyGirlImage = new Image(assets.getTextures().get(Textures.Name.SEXY_GIRL));
                sexyGirlImage.setPosition(imageTwoTable.getWidth() / 2 + 50 , 40);
                final Image pregnantImage = new Image(assets.getTextures().get(Textures.Name.PREGNANT));
                pregnantImage.setPosition(imageTwoTable.getWidth() / 2 + 50, 40);
                final Image manGiveFlowerImage = new Image(assets.getTextures().get(Textures.Name.MAN_GIVE_FLOWER));
                manGiveFlowerImage.setPosition(imageTwoTable.getWidth() / 2 - 100 , 30);
                final Image manRunningImage = new Image(assets.getTextures().get(Textures.Name.MAN_RUNNING));
                manRunningImage.setPosition(imageTwoTable.getWidth() / 2 - 100, 40);
                sexyGirlImage.getColor().a = 0f;
                pregnantImage.getColor().a = 0f;
                manGiveFlowerImage.getColor().a = 0f;
                manRunningImage.getColor().a = 0f;

                imageTwoTable.addActor(sexyGirlImage);
                imageTwoTable.addActor(pregnantImage);
                imageTwoTable.addActor(manGiveFlowerImage);
                imageTwoTable.addActor(manRunningImage);

                sexyGirlImage.addAction(sequence(fadeIn(0.5f), delay(4f), new RunnableAction(){
                    @Override
                    public void run() {
                        manGiveFlowerImage.addAction(fadeIn(0.5f));
                    }
                }, delay(3f), fadeOut(0.5f), new RunnableAction(){
                    @Override
                    public void run() {
                        pregnantImage.addAction(sequence(fadeIn(0.5f), delay(3f), new RunnableAction(){
                            @Override
                            public void run() {
                                manGiveFlowerImage.setVisible(false);
                                manRunningImage.addAction(sequence(fadeIn(0.1f), delay(2f), moveBy(-300, 0, 1f), new RunnableAction(){
                                    @Override
                                    public void run() {
                                        pregnantImage.addAction(sequence(moveBy(-300, 0f, 3f), new RunnableAction(){
                                            @Override
                                            public void run() {
                                                _this.setFinish(true);
                                            }
                                        }));
                                    }
                                }));
                            }
                        }));
                    }
                }));
            }
        };

        runnables.add(run8);

        //////////////////////////////////////////////////////////////////////////////////////////////////

        NotifyRunnable run9 = new NotifyRunnable() {
            @Override
            public void run() {
                final NotifyRunnable _this = this;
                final Image crowdImage = new Image(assets.getTextures().get(Textures.Name.CROWD));
                crowdImage.setPosition(-300 , 40);
                final Image speechImage = new Image(assets.getTextures().get(Textures.Name.SPEECH));
                speechImage.setPosition(imageTwoTable.getWidth() / 2 -30, 180);
                speechImage.setSize(30, 40);

                speechImage.getColor().a = 0f;

                imageTwoTable.addActor(crowdImage);
                imageTwoTable.addActor(speechImage);

                crowdImage.addAction(sequence(moveBy(250, 0, 3f), delay(3f), new RunnableAction(){
                    @Override
                    public void run() {
                        speechImage.addAction(sequence(fadeIn(0.5f), delay(8f), new RunnableAction(){
                            @Override
                            public void run() {
                                speechImage.addAction(fadeOut(1f));
                                crowdImage.addAction(sequence(fadeOut(1f), new RunnableAction(){
                                    @Override
                                    public void run() {
                                        _this.setFinish(true);
                                    }
                                }));
                            }
                        }));
                    }
                }));
            }
        };

        runnables.add(run9);
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        NotifyRunnable run10 = new NotifyRunnable() {
            @Override
            public void run() {
                final NotifyRunnable _this = this;
                final Image ufoImage = new Image(assets.getTextures().get(Textures.Name.UFO));
                final Image rocketImage = new Image(assets.getTextures().get(Textures.Name.ROCKET));
                final Image alienImage = new Image(assets.getTextures().get(Textures.Name.ALIEN));
                ufoImage.setPosition(-300 , 150);
                alienImage.setPosition(imageTwoTable.getWidth() / 2 - alienImage.getPrefWidth() / 2, 20);
                alienImage.getColor().a = 0f;
                rocketImage.setPosition(imageTwoTable.getWidth() / 2 - rocketImage.getPrefWidth() / 2, -150);

                imageTwoTable.addActor(ufoImage);
                imageTwoTable.addActor(alienImage);
                imageTwoTable.addActor(rocketImage);

                ufoImage.addAction(sequence(moveBy(400, 0, 5f), new RunnableAction(){
                    @Override
                    public void run() {
                        alienImage.addAction(sequence(fadeIn(2f), new RunnableAction(){
                            @Override
                            public void run() {
                                ufoImage.addAction(sequence(moveBy(400, 0, 5f), delay(5f), new RunnableAction(){
                                    @Override
                                    public void run() {
                                        rocketImage.addAction(sequence(moveBy(0f, 160f, 4f), new RunnableAction(){
                                            @Override
                                            public void run() {
                                                alienImage.setVisible(false);
                                            }
                                        }, delay(1f), moveBy(0f, 300f, 3f), new RunnableAction(){
                                            @Override
                                            public void run() {
                                                _this.setFinish(true);
                                            }
                                        }));
                                    }
                                }));
                            }
                        }));
                    }
                }));
            }
        };

        runnables.add(run10);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        safeThread = new SafeThread();
        final ArrayList<String> sequence = Strings.split(extra, ",");
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                for(String index : sequence){
                    NotifyRunnable runnable = runnables.get(Integer.valueOf(index));
                    runnable.run();

                    while (!runnable.isFinish()){
                        Threadings.sleep(100);
                        if(safeThread.isKilled()) return;
                    }
                }
            }
        });


    }


    private void moveCat(float yOffset){
        final Image catImage1 = new Image(assets.getTextures().get(Textures.Name.CAT));
        catImage1.setPosition(-300, imageTwoTable.getHeight() / 2 - catImage1.getPrefHeight() / 2 + yOffset);
        imageTwoTable.addActor(catImage1);

        catImage1.addAction(sequence(moveBy(700, 0, 6f)));
    }

    private void moveToRandomPosition(Actor actor, final Runnable onFinish){
        actor.addAction(sequence(moveTo(MathUtils.random(-100, imageTwoTable.getWidth()), MathUtils.random(-100, imageTwoTable.getHeight()), 1f), new RunnableAction(){
            @Override
            public void run() {
                onFinish.run();
            }
        }));
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

    private void resetAllTable(){
        for(int i = imageOneTable.getChildren().size - 1 ; i >=0; i--){
            Actor actor = imageOneTable.getChildren().get(i);
            if(actor.getName() == null || !actor.getName().equals("innerTable")){
                actor.clearActions();
                actor.remove();
            }
        }

        for(int i = imageTwoTable.getChildren().size - 1 ; i >=0; i--){
            Actor actor = imageTwoTable.getChildren().get(i);
            if(actor.getName() == null || !actor.getName().equals("innerTable")){
                actor.clearActions();
                actor.remove();
            }
        }
    }






}
