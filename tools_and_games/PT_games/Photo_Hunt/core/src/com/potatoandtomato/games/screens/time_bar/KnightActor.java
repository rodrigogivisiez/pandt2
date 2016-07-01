package com.potatoandtomato.games.screens.time_bar;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.potatoandtomato.common.assets.AnimationAssets;
import com.potatoandtomato.common.controls.Animator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.Animations;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.enums.KnightState;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.Services;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 12/4/2016.
 */
public class KnightActor extends Table {

    private Table _this;
    private Services services;
    private AnimationAssets animationAssets;
    private Animator knightWalkAnimator, knightRunAnimator, knightAtkAnimator, knightWonAnimator;
    private Container knightContainer;
    private float totalDistance;
    private KnightState currentKnightState;
    private Image iceTopImage, iceBottomImage;
    private Vector2 positionOnStage;
    private float knightFinalX;

    public KnightActor(final Services services, float totalDistance) {
        _this = this;
        this.services = services;
        this.animationAssets = services.getAssets().getAnimations();
        this.totalDistance = totalDistance;

        populate();
    }

    private void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                knightContainer = new Container();
                knightContainer.setTransform(true);
                knightContainer.setDebug(true);
                _this.addActor(knightContainer);


                knightContainer.setPosition(totalDistance, 23);

                knightWalkAnimator = new Animator(0.3f, animationAssets.get(Animations.Name.KNIGHT_WALK));
                knightWalkAnimator.overrideSize(45, 38);

                knightRunAnimator = new Animator(0.3f, animationAssets.get(Animations.Name.KNIGHT_RUN));
                knightRunAnimator.overrideSize(55, 48);

                knightAtkAnimator = new Animator(0.3f, animationAssets.get(Animations.Name.KNIGHT_ATK));
                knightAtkAnimator.overrideSize(52, 54);

                knightWonAnimator = new Animator(0.1f, animationAssets.get(Animations.Name.KNIGHT_WON), false);
                knightWonAnimator.overrideSize(45, 56);

                iceTopImage = new Image(services.getAssets().getTextures().get(Textures.Name.ICE_TOP_HALF));
                iceTopImage.setSize(iceTopImage.getPrefWidth(), iceTopImage.getPrefHeight());
                iceBottomImage = new Image(services.getAssets().getTextures().get(Textures.Name.ICE_BOTTOM_HALF));
                iceBottomImage.setSize(iceBottomImage.getPrefWidth(), iceBottomImage.getPrefHeight());

                knightAtkAnimator.callBackOnIndex(18, new Runnable() {
                    @Override
                    public void run() {
                        services.getSoundsWrapper().playSounds(Sounds.Name.KNIGHT_ATTACKING);
                    }
                });
            }
        });
    }


    public void setKnightAtkSpeed(final float frameRate){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                knightAtkAnimator.getAnimation().setFrameDuration(frameRate);
            }
        });
    }

    public void changeState(final KnightState knightState, final boolean playSound){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(currentKnightState != knightState){
                    if(currentKnightState == KnightState.Attack){
                        knightContainer.setY(knightContainer.getY() - 7);
                    }
                    else if(currentKnightState == KnightState.Won){
                        knightContainer.setY(knightContainer.getY() - 10);
                    }

                    currentKnightState = knightState;
                    knightContainer.clear();
                    if(knightState == KnightState.Walk) {
                        knightContainer.setActor(knightWalkAnimator);
                    }
                    else if(knightState == KnightState.Run){
                        knightContainer.setActor(knightRunAnimator);
                        if(playSound) services.getSoundsWrapper().playSounds(Sounds.Name.KNIGHT_RUN);
                    }
                    else if(knightState == KnightState.Attack){
                        knightContainer.setY(knightContainer.getY() + 7);
                        knightContainer.setActor(knightAtkAnimator);
                    }
                    else if(knightState == KnightState.Won){
                        knightContainer.setY(knightContainer.getY() + 10);
                        knightContainer.setActor(knightWonAnimator);
                        services.getSoundsWrapper().playSounds(Sounds.Name.KNIGHT_WON);
                    }
                }
            }
        });

    }

    public void setFreeze(final boolean freezed){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(freezed){
                    stopAnimation();

                    iceTopImage.clearActions();
                    iceBottomImage.clearActions();
                    iceTopImage.remove();
                    iceBottomImage.remove();
                    iceTopImage.getColor().a = 1f;
                    iceBottomImage.getColor().a = 1f;

                    float x = knightFinalX;
                    float y = knightContainer.getY() + knightContainer.getHeight() / 2;

                    iceTopImage.setPosition(x - iceTopImage.getWidth() / 2, y + 24);
                    iceBottomImage.setPosition(x - iceBottomImage.getWidth() / 2, y - 50);

                    iceTopImage.addAction(Actions.moveBy(0, -25, 0.2f));
                    iceBottomImage.addAction(Actions.moveBy(0, +25.3f, 0.2f));

                    _this.addActor(iceTopImage);
                    _this.addActor(iceBottomImage);

                }
                else{
                    iceTopImage.addAction(sequence(fadeOut(0.1f), new RunnableAction(){
                        @Override
                        public void run() {
                            iceTopImage.remove();
                        }
                    }));
                    iceBottomImage.addAction(sequence(fadeOut(0.1f), new RunnableAction(){
                        @Override
                        public void run() {
                            iceBottomImage.remove();
                        }
                    }));
                    continueAnimation();
                }
            }
        });


    }

    public void setKnightPositionX(final float x, boolean autoChangeState, final boolean animate){
        if(autoChangeState){
            float toMovedDistance = Math.abs(x - knightContainer.getX());

            if(toMovedDistance > 10 && x > 5){
                changeState(KnightState.Run, animate);
            }
            else if(toMovedDistance <= 10 && toMovedDistance > 0 && x > 5){
                changeState(KnightState.Walk, animate);
            }
            else{
               changeState(KnightState.Attack, animate);
            }
        }

        knightFinalX = x;
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                knightContainer.addAction(moveTo(x, knightContainer.getY(), animate ? 0.3f : 0f));
            }
        });

    }

    public void continueAnimation(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                knightWalkAnimator.setPaused(false);
                knightRunAnimator.setPaused(false);
                knightAtkAnimator.setPaused(false);
            }
        });

    }

    public void stopAnimation(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                knightWalkAnimator.setPaused(true);
                knightRunAnimator.setPaused(true);
                knightAtkAnimator.setPaused(true);
            }
        });

    }

    public Vector2 getPositionOnStage(){
        if(positionOnStage == null){
            positionOnStage = Positions.actorLocalToStageCoord(this);
        }
        return positionOnStage;
    }

    public void popStars(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                int totalStars = MathUtils.random(3, 8);
                for(int i = 0; i < totalStars; i++){
                    final Image starImage = new Image(services.getAssets().getTextures().get(Textures.Name.SMALL_STAR_ICON));
                    Vector2 startingPosition = new Vector2(MathUtils.random(-25f, -20f), MathUtils.random(20f, 30f));
                    Vector2 middlePosition = new Vector2(MathUtils.random(-40f, 0f), MathUtils.random(startingPosition.y, 50f));

                    float finalPositionX;
                    if(middlePosition.x > startingPosition.x){
                        finalPositionX = MathUtils.random(middlePosition.x + 10, 10f);
                    }
                    else{
                        finalPositionX = MathUtils.random(-50f, middlePosition.x - 10);
                    }

                    Vector2 finalPosition = new Vector2(finalPositionX, MathUtils.random(0f, middlePosition.y - 10f));

                    starImage.setPosition(startingPosition.x, startingPosition.y);
                    starImage.setSize(starImage.getPrefWidth(), starImage.getPrefHeight());
                    starImage.addAction(sequence(Actions.moveTo(middlePosition.x, middlePosition.y, MathUtils.random(0.4f, 0.5f)),
                            parallel(Actions.moveTo(finalPosition.x, finalPosition.y, MathUtils.random(0.4f, 0.5f)), fadeOut(MathUtils.random(0.5f, 0.7f))), new RunnableAction(){
                                @Override
                                public void run() {
                                    starImage.remove();
                                }
                            }));

                    _this.addActor(starImage);
                }
            }
        });


    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(currentKnightState == KnightState.Run){
            final Image image = new Image(knightRunAnimator.getCurrentFrame());
            image.setSize(55, 48);
            image.getColor().a = 0.3f;
            image.setPosition(knightContainer.getX() - image.getWidth() / 2, knightContainer.getY() - image.getHeight() / 2);
            this.addActor(image);
            image.addAction(sequence(fadeOut(0.3f), new RunnableAction(){
                @Override
                public void run() {
                    image.remove();
                }
            }));
        }
    }

    public Animator getKnightAtkAnimator() {
        return knightAtkAnimator;
    }

    public Animator getKnightWalkAnimator() {
        return knightWalkAnimator;
    }

    public Animator getKnightRunAnimator() {
        return knightRunAnimator;
    }
}
