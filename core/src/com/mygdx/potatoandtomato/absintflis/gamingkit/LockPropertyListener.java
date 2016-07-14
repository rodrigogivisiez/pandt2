package com.mygdx.potatoandtomato.absintflis.gamingkit;

/**
 * Created by SiongLeng on 22/6/2016.
 */
public abstract class LockPropertyListener {

    private String property;

    public LockPropertyListener(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public abstract void onLockSucceed();

    public void onUnLockSucceed(){}

}
