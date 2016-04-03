package com.potatoandtomato.common.absints;

/**
 * Created by SiongLeng on 4/4/2016.
 */
public abstract class ConnectionMonitorListener {

    public abstract void onExceedReconnectLimitTime(String userId);

}
