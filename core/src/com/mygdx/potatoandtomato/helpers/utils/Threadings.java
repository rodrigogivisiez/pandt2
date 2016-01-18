package com.mygdx.potatoandtomato.helpers.utils;

import com.badlogic.gdx.Gdx;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class Threadings {

    private static long mainTreadId;
    private static boolean isRunning;
    private static float continuousRenderPeriod;
    private static boolean continuousRenderLock;
    private static long lastChangedLock;

    public static void setMainTreadId(){
        mainTreadId = Thread.currentThread().getId();
    }

    public static Thread runInBackground(Runnable toRun){
        Thread t = new Thread(toRun);
        t.start();
        return t;
    }



    public static void delay(final long timeInMs, final Runnable toRun){
        runInBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(timeInMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        toRun.run();
                    }
                });
            }
        });
    }

    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void postRunnable(Runnable runnable){
        if(Thread.currentThread().getId() != mainTreadId){
            Gdx.app.postRunnable(runnable);
        }
        else{
            runnable.run();
        }
    }

    public static void renderFor(final float sec){
        if(continuousRenderLock) return;

        continuousRenderPeriod += sec;

        if(Gdx.graphics.isContinuousRendering()) return;
        else{
            if(isRunning){
                return;
            }
        }

        Threadings.runInBackground(new Runnable() {
            float total = 0;
            @Override
            public void run() {
                isRunning = true;
                Gdx.graphics.setContinuousRendering(true);
                while (total < continuousRenderPeriod){
                    total += Gdx.graphics.getDeltaTime();
                }
                if(!continuousRenderLock) Gdx.graphics.setContinuousRendering(false);
                isRunning = false;
                continuousRenderPeriod = 0;
            }
        });
    }

    public static void setContinuousRenderLock(boolean continuousRenderLock) {

        if(continuousRenderLock){
            Gdx.graphics.setContinuousRendering(true);
            lastChangedLock = System.currentTimeMillis();
            Threadings.continuousRenderLock = true;
        }
        else{
            if(lastChangedLock == 0 || System.currentTimeMillis() - lastChangedLock > 500) {
                Gdx.graphics.setContinuousRendering(false);
                Threadings.continuousRenderLock = false;
            }
        }
    }
}


