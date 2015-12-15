package com.mygdx.potatoandtomato.absintflis.gamingkit;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public abstract class ConnectionChangedListener {

    public enum Status{
        CONNECTED, DISCONNECTED
    }

    public ConnectionChangedListener() {

    }

    public abstract void onChanged(Status st);

}
