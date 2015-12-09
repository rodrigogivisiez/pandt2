package com.mygdx.potatoandtomato.helpers.utils;

import com.badlogic.gdx.Gdx;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class Threadings {

    public static void runInBackground(Runnable toRun){
        new Thread(toRun).start();
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

}
