package com.mygdx.potatoandtomato.absintflis;

/**
 * Created by SiongLeng on 19/12/2015.
 */
public abstract class OnQuitListener {

    public enum Result{
        YES, NO
    }

    public abstract void onResult(Result result);


}
