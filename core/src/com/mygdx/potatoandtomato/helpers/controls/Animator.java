package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/**
 * Created by SiongLeng on 18/6/2015.
 */
public class Animator extends Actor {

    float frameDuration;
    float elapsed = 0;
    Animation animation;

    public Animator(float frameDuration, Array<? extends TextureRegion> keyFrames) {
        this.frameDuration = frameDuration;
        animation = new Animation(frameDuration, keyFrames);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        elapsed += Gdx.graphics.getDeltaTime();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        elapsed += Gdx.graphics.getDeltaTime();
        batch.draw(animation.getKeyFrame(elapsed, true), getX() , getY(), getOriginX(), getOriginY(), getWidth(),
                                            getHeight(), getScaleX(), getScaleY(), getRotation());

    }


}
