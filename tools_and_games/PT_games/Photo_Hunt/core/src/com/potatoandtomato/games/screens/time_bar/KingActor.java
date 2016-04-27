package com.potatoandtomato.games.screens.time_bar;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.potatoandtomato.common.assets.AnimationAssets;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.controls.Animator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.Animations;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.enums.KingState;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 12/4/2016.
 */
public class KingActor extends Table {

    private Table _this;
    private Services services;
    private MyAssets assets;
    private AnimationAssets animations;
    private Animator normalAnimator, panicAnimator, winAnimator, loseAnimator;
    private KingState currentKingState;

    public KingActor(final Services services) {
        _this = this;
        this.services = services;
        this.assets = services.getAssets();
        this.animations = services.getAssets().getAnimations();
        this.setTouchable(Touchable.disabled);
        normalAnimator = new Animator(0.1f, this.animations.get(Animations.Name.KING_NORMAL));
        normalAnimator.overrideSize(28, 40);

        panicAnimator = new Animator(0.1f, this.animations.get(Animations.Name.KING_PANIC));
        panicAnimator.overrideSize(58, 40);

        winAnimator = new Animator(0.1f, this.animations.get(Animations.Name.KING_WIN));
        winAnimator.overrideSize(31, 40);

        loseAnimator = new Animator(0.20f, this.animations.get(Animations.Name.KING_LOSE), false);
        loseAnimator.overrideSize(34, 40);

        changeState(KingState.Normal);

    }

    public void changeState(final KingState kingState){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(currentKingState != kingState){
                    currentKingState = kingState;
                    if(kingState == KingState.Normal){
                        _this.clear();
                        _this.add(normalAnimator);
                    }
                    else if(kingState == KingState.Panic){
                        _this.clear();
                        _this.add(panicAnimator).padLeft(-10);
                    }
                    else if(kingState == KingState.Win){
                        _this.clear();
                        _this.add(winAnimator).padLeft(-2);
                    }
                    else if(kingState == KingState.Lose){
                        _this.clear();
                        _this.add(loseAnimator).padLeft(-6);
                    }
                }
            }
        });

    }

    public void stopAnimation(){
        normalAnimator.setPaused(true);
        panicAnimator.setPaused(true);
    }

    public void continueAnimation(){
        normalAnimator.setPaused(false);
        panicAnimator.setPaused(false);
    }

















}
