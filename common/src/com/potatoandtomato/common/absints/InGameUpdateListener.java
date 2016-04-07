package com.potatoandtomato.common.absints;

/**
 * Created by SiongLeng on 27/12/2015.
 */
public abstract class InGameUpdateListener {

    public abstract void onUpdateReceived(String msg, String senderId);

}
