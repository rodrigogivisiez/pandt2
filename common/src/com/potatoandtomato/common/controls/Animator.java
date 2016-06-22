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
    int previousIndex;
    boolean looping;

    public Animator(float frameDuration, Array<? extends TextureRegion> keyFrames) {
        this(frameDuration, keyFrames, true);
    }

    public Animator(float frameDuration, Array<? extends TextureRegion> keyFrames, boolean looping) {
        this.frameDuration = frameDuration;
        this.keyFrames = keyFrames;
        previousIndex = -1;
        animation = new Animation(frameDuration, keyFrames);
        this.looping = looping;
        if(looping){
            animation.setPlayMode(Animation.PlayMode.LOOP);
        }
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

    public void replay(){
        elapsed = 0;
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

    public void setElapsed(float elapsed) {
        this.elapsed = elapsed;
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

        batch.draw(animation.getKeyFrame(elapsed, looping), getX() , getY(), getOriginX(), getOriginY(), getWidth(),
                                            getHeight(), getScaleX(), getScaleY(), getRotation());

        int currentIndex = animation.getKeyFrameIndex(elapsed);
        if(previousIndex != currentIndex){
            if(currentIndex < previousIndex){
                if(callBackOnIndexMap.containsKey(currentIndex)){
                    callBackOnIndexMap.get(currentIndex).run();
                }
            }
            else{
                for(int i = previousIndex + 1; i <= currentIndex; i++){
                    if(callBackOnIndexMap.containsKey(i)){
                        callBackOnIndexMap.get(i).run();
                    }
                }
            }
            previousIndex = currentIndex;

        }

        if(this.getDebug()){
            System.out.println(previousIndex);
        }


    }

    public enum IndexType{
        First, Last
    }


}
