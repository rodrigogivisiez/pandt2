package com.potatoandtomato.games.screens.scores;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Patches;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.models.Services;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 14/4/2016.
 */
public class ScoresActor extends Table {

    private Table _this;
    private Services services;
    private MyAssets assets;
    private GameCoordinator gameCoordinator;
    private Label mainScoreLabel;
    private Label nextHighScoreLabel;
    private Array<Actor> poppedActorsArray;
    private Table popRulerActor;
    private Stage stage;

    public ScoresActor(Services services, GameCoordinator gameCoordinator) {
        _this = this;
        this.services = services;
        this.assets = services.getAssets();
        this.gameCoordinator = gameCoordinator;
        this.poppedActorsArray = new Array();
        this.align(Align.left);
    }

    public void populate(int currentScore){
        Label.LabelStyle mainScoreLabelStyle = new Label.LabelStyle(assets.getFonts().get(
                                                        Fonts.FontId.ENCHANTED_MAX_REGULAR_B_FFFFFF_563500_4), Color.valueOf("ffe9c0"));
        mainScoreLabel = new Label(String.format("%,d", currentScore), mainScoreLabelStyle);

        this.add(mainScoreLabel).padLeft(10);

        Table nextHighScoreTable = new Table();
        nextHighScoreTable.align(Align.right);

        Label.LabelStyle smallLabelStyle = new Label.LabelStyle(assets.getFonts().get(
                                              Fonts.FontId.ENCHANTED_XL_REGULAR), Color.WHITE);

        Label captionLabel = new Label(services.getTexts().nextHighScore(), smallLabelStyle);

        Image separatorImage = new Image(assets.getTextures().get(Textures.Name.SCRATCH_SEPARATOR));

        Label.LabelStyle nextHighScoreLabelStyle = new Label.LabelStyle(assets.getFonts().get(
                                    Fonts.FontId.ENCHANTED_XXL_REGULAR), Color.WHITE);

        nextHighScoreLabel = new Label("", nextHighScoreLabelStyle);
        setNextHighScore(-1);

        nextHighScoreTable.add(captionLabel);
        nextHighScoreTable.row();
        nextHighScoreTable.add(separatorImage).padTop(-7);
        nextHighScoreTable.row();
        nextHighScoreTable.add(nextHighScoreLabel).padTop(-7);

        this.add(nextHighScoreTable).expand().fill().padRight(5);
    }

    public void setMainScore(final int score){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                mainScoreLabel.setText(String.format("%,d", score));
            }
        });
    }

    public void setNextHighScore(final int nextHighScore){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                nextHighScoreLabel.setText(nextHighScore == -1 ? "-" : String.format("%,d", nextHighScore));
            }
        });
    }

    public void popRulerScoreOnPosition(final float x, final float y, final int width){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(popRulerActor == null){
                    popRulerActor = new Table();
                    popRulerActor.setBackground(new NinePatchDrawable(assets.getPatches().get(Patches.Name.RED_ARROW)));

                    Label.LabelStyle meterLabelStyle = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.MYRIAD_XS_REGULAR_B_ffffff_000000_1),
                            Color.valueOf("fc0000"));
                    Label meterLabel = new Label("", meterLabelStyle);
                    meterLabel.setName("meterLabel");
                    meterLabel.setAlignment(Align.center);
                    popRulerActor.add(meterLabel).padBottom(-20).expandX().fillX();

                    popRulerActor.setPosition(x, y);

                    if(stage == null) stage = _this.getStage();
                    stage.addActor(popRulerActor);
                }

                popRulerActor.setSize(width, 10);
                ((Label) popRulerActor.findActor("meterLabel")).setText(width + "m");
            }
        });

    }

    public void popScoreOnPosition(final float x, final float y, final String msg, final boolean isSpecial, final Runnable onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Label.LabelStyle scoreStyle = new Label.LabelStyle(
                        assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR_B_ffffff_000000_2),
                        isSpecial ? Color.YELLOW : Color.WHITE);

                boolean isZero = false;

                String finalMsg = msg;
                if(Strings.isNumeric(finalMsg)){
                    if(finalMsg.equals("0")) isZero = true;
                    finalMsg = "+" + finalMsg;
                }

                Label scoreLabel = new Label(finalMsg, scoreStyle);

                Container container = new Container();
                container.setTransform(true);
                container.setPosition(x, y);
                container.setOrigin(Align.center);
                container.setActor(scoreLabel);

                if(isZero){
                    container.getColor().a = 0f;
                }

                container.setScale(0, 0);

                if(stage == null) stage = _this.getStage();
                stage.addActor(container);
                container.addAction(sequence(parallel(Actions.rotateBy(720, 0.6f), Actions.scaleTo(1, 1, 0.8f, Interpolation.bounceOut)), new RunnableAction(){
                    @Override
                    public void run() {
                        if(onFinish != null) onFinish.run();
                    }
                }));

                poppedActorsArray.add(container);
            }
        });
    }

    public void clearAllPopScores(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(Actor actor : poppedActorsArray){
                    actor.remove();
                }
                poppedActorsArray.clear();
                if(popRulerActor != null){
                    popRulerActor.remove();
                    popRulerActor = null;
                }
            }
        });
    }
}
