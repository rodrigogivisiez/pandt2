package com.potatoandtomato.games.screens.papyruses;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.Services;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sizeBy;

/**
 * Created by SiongLeng on 23/5/2016.
 */
public class BeforeStartPapyrusScene extends PapyrusSceneAbstract {

    private Services services;
    private MyAssets assets;
    private Table root;
    private Table _this;
    private Animator knightAnimator;
    private Container messageLabelContainer;
    private Image gearIcon;
    private Label messageLabel;
    private boolean isContinue;

    public BeforeStartPapyrusScene(Services services, Table root, boolean isContinue) {
        this.isContinue = isContinue;
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

                Image startGameSceneImage = new Image(assets.getTextures().get(Textures.Name.START_GAME_SCENE));

                knightAnimator = new Animator(0.02f, assets.getAnimations().get(Animations.Name.KNIGHT_HANDUP), false);
                knightAnimator.setPaused(true);
                knightAnimator.setPosition(Positions.centerX(root.getWidth(), knightAnimator.getWidth()) + 15, 25);
                knightAnimator.addAction(forever(sequence(Actions.moveBy(-0.3f, -0.3f, 0.1f), Actions.moveBy(0.3f, 0.3f, 0.1f))));

                Table messageTable = new Table();
                messageTable.align(Align.right);
                messageTable.setSize(root.getWidth(), 30);
                messageTable.setPosition(0, root.getHeight() - 40);

                gearIcon = new Image(assets.getTextures().get(Textures.Name.GEAR_ICON));
                gearIcon.setOrigin(Align.center);
                gearIcon.addAction(forever(Actions.rotateBy(1, 0.1f)));


                messageLabel = new Label(isContinue ? services.getTexts().waitingForContinue() : services.getTexts().evilKnightPreparing(),
                            new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.ENCHANTED_XXL_REGULAR), Color.BLACK));
                messageLabelContainer = new Container(messageLabel);
                messageLabelContainer.setTransform(true);

                messageTable.add(gearIcon).padRight(5);
                messageTable.add(messageLabelContainer).padRight(50);

                _this.add(startGameSceneImage);
                _this.addActor(knightAnimator);
                _this.addActor(messageTable);

                services.getSoundsWrapper().playMusic(Sounds.Name.BEFORE_START_GAME_MUSIC);
            }
        });
    }

    public void gameReadyToStart(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                gearIcon.setVisible(false);
                messageLabel.setText("EVIL KNIGHT IS COMING!!!");
                messageLabelContainer.addAction(Actions.moveBy(-20f, 0f, 0.5f, Interpolation.bounceOut));

                knightAnimator.setPaused(false);

                knightAnimator.callBackOnIndex(10, new Runnable() {
                    @Override
                    public void run() {
                        services.getSoundsWrapper().playSounds(Sounds.Name.KNIGHT_ARMOR);
                    }
                });

                knightAnimator.callBackOnIndex(Animator.IndexType.Last, new Runnable() {
                    @Override
                    public void run() {
                        services.getSoundsWrapper().playSounds(Sounds.Name.KNIGHT_WON);
                    }
                });

                if(isContinue){
                    shouldCloseInMiliSecs(0);
                }
                else{
                    shouldCloseInMiliSecs(3000);
                }

            }
        });
    }


    @Override
    public void dispose() {
        super.dispose();
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                services.getSoundsWrapper().stopMusic(Sounds.Name.BEFORE_START_GAME_MUSIC);
            }
        });
    }
}
