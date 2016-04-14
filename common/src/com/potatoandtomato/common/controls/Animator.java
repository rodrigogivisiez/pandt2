package com.potatoandtomato.common.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * Created by SiongLeng on 18/6/2015.
 */
public class Animator extends Actor {

    Array<? extends TextureRegion> keyFrames;
    float frameDuration;
    float elapsed = 0;
    Animation animation;
    float overRiddenWidth, overRiddenHeight;
    boolean paused;
    HashMap<Integer, Runnable> callBackOnIndexMap;
    int currentIndex;

    public Animator(float frameDuration, Array<? extends TextureRegion> keyFrames) {
        this.frameDuration = frameDuration;
        this.keyFrames = keyFrames;
        currentIndex = -1;
        animation = new Animation(frameDuration, keyFrames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        callBackOnIndexMap = new HashMap();
    }

    public void callBackOnIndex(IndexType type, Runnable runnable){
        int index = 0;
        if(type == IndexType.Last){
            index = keyFrames.size - 1;
        }

        callBackOnIndex(index, runnable);
    }

    public void callBackOnIndex(int index, Runnable runnable){
        callBackOnIndexMap.put(index, runnable);
    }

    public Animation getAnimation() {
        return animation;
    }

    public void overrideSize(float width, float height){
        overRiddenWidth = width;
        overRiddenHeight = height;
    }

    public float getWidth(){
        if(overRiddenWidth == 0){
            return keyFrames.get(0).getRegionWidth();
        }
        else{
            return overRiddenWidth;
        }
    }

    public float getHeight(){
        if(overRiddenHeight == 0){
            return keyFrames.get(0).getRegionHeight();
        }
        else{
            return overRiddenHeight;
        }
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public TextureRegion getCurrentFrame(){
        return animation.getKeyFrame(elapsed, true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(!paused){
            elapsed += Gdx.graphics.getDeltaTime();
        }

        batch.draw(animation.getKeyFrame(elapsed, true), getX() , getY(), getOriginX(), getOriginY(), getWidth(),
                                            getHeight(), getScaleX(), getScaleY(), getRotation());

        if(currentIndex != animation.getKeyFrameIndex(elapsed)){
            currentIndex = animation.getKeyFrameIndex(elapsed);
            if(callBackOnIndexMap.containsKey(currentIndex)){
                callBackOnIndexMap.get(currentIndex).run();
            }
        }

        if(this.getDebug()){
            System.out.println(currentIndex);
        }


    }

    public enum IndexType{
        First, Last
    }


}
