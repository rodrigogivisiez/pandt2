package com.potatoandtomato.games.screens.time_bar;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.potatoandtomato.common.assets.AnimationAssets;
import com.potatoandtomato.common.controls.Animator;
import com.potatoandtomato.games.assets.Animations;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.enums.KnightState;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.Services;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 12/4/2016.
 */
public class KnightActor extends Table {

    private Services services;
    private AnimationAssets animationAssets;
    private Animator knightWalkAnimator, knightRunAnimator, knightAtkAnimator;
    private Container knightContainer;
    private float totalDistance;
    private KnightState currentKnightState;
    private Image iceTopImage, iceBottomImage;
    private Vector2 positionOnStage;

    public KnightActor(Services services, float totalDistance) {
        this.services = services;
        this.animationAssets = services.getAssets().getAnimations();
        this.knightContainer = new Container();
        knightContainer.setTransform(true);
        knightContainer.setDebug(true);
        this.addActor(knightContainer);
        this.totalDistance = totalDistance;

        knightContainer.setPosition(totalDistance, 23);

        knightWalkAnimator = new Animator(0.3f, animationAssets.get(Animations.Name.KNIGHT_WALK));
        knightWalkAnimator.overrideSize(45, 38);

        knightRunAnimator = new Animator(0.3f, animationAssets.get(Animations.Name.KNIGHT_RUN));
        knightRunAnimator.overrideSize(55, 48);

        knightAtkAnimator = new Animator(0.3f, animationAssets.get(Animations.Name.KNIGHT_ATK));
        knightAtkAnimator.overrideSize(47, 47);

        iceTopImage = new Image(services.getAssets().getTextures().get(Textures.Name.ICE_TOP_HALF));
        iceTopImage.setSize(iceTopImage.getPrefWidth(), iceTopImage.getPrefHeight());
        iceBottomImage = new Image(services.getAssets().getTextures().get(Textures.Name.ICE_BOTTOM_HALF));
        iceBottomImage.setSize(iceBottomImage.getPrefWidth(), iceBottomImage.getPrefHeight());
    }

    public void setKnightAtkSpeed(float frameRate){
        knightAtkAnimator.getAnimation().setFrameDuration(frameRate);
    }

    public void changeState(KnightState knightState){
        if(currentKnightState != knightState){
            currentKnightState = knightState;
            knightContainer.clear();
            if(knightState == KnightState.Walk){
                knightContainer.setActor(knightWalkAnimator);
            }
            else if(knightState == KnightState.Run){
                knightContainer.setActor(knightRunAnimator);
            }
            else if(knightState == KnightState.Attack){
                knightContainer.setY(knightContainer.getY() + 5);
                knightContainer.setActor(knightAtkAnimator);
            }
        }
    }

    public void setFreeze(boolean freezed){
        if(freezed){
            stopAnimation();

            iceTopImage.clearActions();
            iceBottomImage.clearActions();
            iceTopImage.remove();
            iceBottomImage.remove();
            iceTopImage.getColor().a = 1f;
            iceBottomImage.getColor().a = 1f;

            float x = knightContainer.getX();
            float y = knightContainer.getY() + knightContainer.getHeight() / 2;

            if(x < 1) x = 10;

            iceTopImage.setPosition(x - iceTopImage.getWidth() / 2, y + 24);
            iceBottomImage.setPosition(x - iceBottomImage.getWidth() / 2, y - 50);

            iceTopImage.addAction(Actions.moveBy(0, -25, 0.2f));
            iceBottomImage.addAction(Actions.moveBy(0, +25, 0.2f));

            this.addActor(iceTopImage);
            this.addActor(iceBottomImage);

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

    public void setKnightPositionX(float x, boolean autoChangeState){
        if(autoChangeState){
            float toMovedDistance = Math.abs(x - knightContainer.getX());

            if(toMovedDistance > 10 && x > 5){
                changeState(KnightState.Run);
            }
            else if(toMovedDistance <= 10 && toMovedDistance > 0 && x > 5){
                changeState(KnightState.Walk);
            }
            else{
               changeState(KnightState.Attack);
            }
        }

        knightContainer.addAction(moveTo(x, knightContainer.getY(), 0.3f));
    }

    public void continueAnimation(){
        knightWalkAnimator.setPaused(false);
        knightRunAnimator.setPaused(false);
        knightAtkAnimator.setPaused(false);
    }

    public void stopAnimation(){
        knightWalkAnimator.setPaused(true);
        knightRunAnimator.setPaused(true);
        knightAtkAnimator.setPaused(true);
    }

    public Vector2 getPositionOnStage(){
        if(positionOnStage == null){
            positionOnStage = Positions.actorLocalToStageCoord(this);
        }
        return positionOnStage;
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
}
