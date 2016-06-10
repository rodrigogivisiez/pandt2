package com.mygdx.potatoandtomato.absintflis.scenes;

import com.mygdx.potatoandtomato.enums.ConnectionStatus;

/**
 * Created by SiongLeng on 7/6/2016.
 */
public abstract class PlayerConnectionStateListener {

    public abstract void onPlayerDisconnectTimeout(String userId);

    public abstract void onPlayerConnectionChanged(String userId, ConnectionStatus connectionStatus);

}
