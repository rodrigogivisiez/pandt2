package com.mygdx.potatoandtomato.helpers.utils;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class SafeThread {

    volatile boolean isKilled = false;

    public void kill() {
        isKilled = true;
    }

    public boolean isKilled() {
        return isKilled;
    }
}
