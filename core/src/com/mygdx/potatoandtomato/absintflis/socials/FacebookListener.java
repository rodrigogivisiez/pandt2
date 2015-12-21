package com.mygdx.potatoandtomato.absintflis.socials;

/**
 * Created by SiongLeng on 21/12/2015.
 */
public abstract class FacebookListener {

    public enum Result{
        SUCCESS, FAILED
    }

    public void onLoginComplete(Result result){

    }

    public void onLogoutComplete(Result result){

    }

}
