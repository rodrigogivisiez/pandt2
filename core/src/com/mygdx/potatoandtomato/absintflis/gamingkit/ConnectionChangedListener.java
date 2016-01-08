package com.mygdx.potatoandtomato.absintflis.gamingkit;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public abstract class ConnectionChangedListener {

    public enum ConnectStatus {
        CONNECTED, DISCONNECTED
    }

    public ConnectionChangedListener() {

    }

    public abstract void onChanged(ConnectStatus st);

}
