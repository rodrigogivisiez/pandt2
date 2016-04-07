package com.potatoandtomato.common.absints;

/**
 * Created by SiongLeng on 7/1/2016.
 */
public abstract class UserStateListener {


    public abstract void userAbandoned(String userId);
    public abstract void userConnected(String userId);
    public abstract void userDisconnected(String userId);

}
