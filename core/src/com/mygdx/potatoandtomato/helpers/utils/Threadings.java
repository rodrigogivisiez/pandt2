package com.mygdx.potatoandtomato.helpers.utils;

import com.badlogic.gdx.Gdx;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class Threadings {

    private static long mainTreadId;

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



}


