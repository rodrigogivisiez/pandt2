package com.potatoandtomato.common.utils;

/**
 * Created by SiongLeng on 22/3/2016.
 */
public class OneTimeRunnable {

    private Runnable toRun;
    private boolean runFinish;

    public OneTimeRunnable(Runnable toRun) {
        this.toRun = toRun;
    }

    public void run(){
        if(!runFinish){
            runFinish = true;
            toRun.run();
        }
    }

    public boolean isRunFinish() {
        return runFinish;
    }
}
