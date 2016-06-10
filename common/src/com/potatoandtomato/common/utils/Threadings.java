package com.potatoandtomato.common.utils;

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
    public static int waitingTaskCount = 0;

    public static void setMainTreadId(){
        mainTreadId = Thread.currentThread().getId();
    }

    public static Thread runInBackground(Runnable toRun){
        Thread t = new Thread(toRun);
        t.start();
        return t;
    }


    public static ThreadFragment delay(final long timeInMs, final Runnable toRun){
        final ThreadFragment delayFrag = new ThreadFragment();
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
                        delayFrag.setFinished(true);
                    }
                });
            }
        });
        return delayFrag;
    }

    public static void delayNoPost(final long timeInMs, final Runnable toRun){
        runInBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(timeInMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                toRun.run();
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

    public static void waitTasks(int expectedTask){
        waitingTaskCount = 0;
        while (expectedTask > waitingTaskCount){
            sleep(300);
        }
        waitingTaskCount = 0;
    }

    public static void oneTaskFinish(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                waitingTaskCount++;
            }
        });
    }

    public static SafeThread countDown(final int totalSecs, final int notifyPeriodInMiliSecs, final RunnableArgs<Integer> onNotify){
        final SafeThread safeThread = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int i = totalSecs;
                while (true){
                    i--;

                    onNotify.run(i);

                    if(i == 0 || safeThread.isKilled()){
                        break;
                    }
                    Threadings.sleep(notifyPeriodInMiliSecs);

                }
            }
        });
        return safeThread;
    }


    public static class ThreadFragment{

        boolean finished;

        public boolean isFinished() {
            return finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }
    }



}


