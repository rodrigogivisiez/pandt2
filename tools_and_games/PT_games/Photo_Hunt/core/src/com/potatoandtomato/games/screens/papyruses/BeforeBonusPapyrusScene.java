package com.potatoandtomato.games.screens.papyruses;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.controls.Animator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.*;
import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.Services;
import javafx.geometry.Pos;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 24/5/2016.
 */
public class BeforeBonusPapyrusScene extends PapyrusSceneAbstract{

    private Services services;
    private MyAssets assets;
    private Table root;
    private Table _this;
    private Label messageLabel;

    public BeforeBonusPapyrusScene(Services services, Table root) {
        this.services = services;
        this.root = root;
        this.assets = services.getAssets();
        _this = this;

        populate();
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                _this.addAction(forever(sequence(Actions.moveBy(-0.4f, -0.4f, 0.1f), Actions.moveBy(0.4f, 0.4f, 0.1f))));

                Image trumpetSceneImage = new Image(assets.getTextures().get(Textures.Name.TRUMPET));

                Image specialStageImage = new Image(assets.getTextures().get(Textures.Name.SPECIAL_STAGE));
                specialStageImage.setPosition(Positions.centerX(root.getWidth(), specialStageImage.getPrefWidth()), root.getHeight() + 100);
                specialStageImage.addAction(sequence(delay(2f), Actions.moveBy(0, -250, 5f)));

                final Table tracesTable = new Table();
                tracesTable.setSize(root.getWidth(), root.getHeight());
                tracesTable.getColor().a = 0f;
                tracesTable.addAction(forever(sequence(delay(0.8f), fadeIn(0f), delay(0.2f), fadeOut(0f))));
                Threadings.delay(7 * 1000, new Runnable() {
                    @Override
                    public void run() {
                        tracesTable.clearActions();
                        tracesTable.getColor().a = 1f;
                    }
                });

                messageLabel = new Label("",
                                    new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.ENCHANTED_MAX_REGULAR), Color.BLACK));
                messageLabel.setAlignment(Align.center);
                messageLabel.setWrap(true);
                messageLabel.getColor().a = 0f;
                messageLabel.addAction(sequence(delay(7f), fadeIn(0.5f)));

                Table messageTable = new Table();
                messageTable.setSize(300, 50);
                messageTable.setPosition(Positions.centerX(root.getWidth(), messageTable.getWidth()), 30);
                messageTable.add(messageLabel).expandX().fillX();

                Image trumpetLeftTraceImage = new Image(assets.getTextures().get(Textures.Name.TRUMPET_LEFT_TRACE));
                trumpetLeftTraceImage.setPosition(112, 47);

                Image trumpetRightTraceImage = new Image(assets.getTextures().get(Textures.Name.TRUMPET_RIGHT_TRACE));
                trumpetRightTraceImage.setPosition(459, 48);

                tracesTable.addActor(trumpetLeftTraceImage);
                tracesTable.addActor(trumpetRightTraceImage);

                _this.add(trumpetSceneImage);
                _this.addActor(tracesTable);
                _this.addActor(specialStageImage);
                _this.addActor(messageTable);

                services.getSoundsWrapper().playMusicNoLoop(Sounds.Name.BEFORE_BONUS_MUSIC);

                shouldCloseInMiliSecs(14500);

            }
        });
    }

    public void revealBonus(final BonusType bonusType, String extra){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                messageLabel.setText(bonusType.name());
            }
        });
    }


    @Override
    public void dispose() {
        super.dispose();
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                services.getSoundsWrapper().stopMusic(Sounds.Name.BEFORE_BONUS_MUSIC);
            }
        });
    }
}