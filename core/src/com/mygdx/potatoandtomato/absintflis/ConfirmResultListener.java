package com.mygdx.potatoandtomato.absintflis;

/**
 * Created by SiongLeng on 14/12/2015.
 */
public abstract class ConfirmResultListener {

    public enum Result{
        YES, NO, CANCEL
    }

    public abstract void onResult(Result result);

}
