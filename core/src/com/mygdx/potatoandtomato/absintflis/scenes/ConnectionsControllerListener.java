package com.mygdx.potatoandtomato.absintflis.scenes;

import com.mygdx.potatoandtomato.enums.GameConnectionStatus;

/**
 * Created by SiongLeng on 7/6/2016.
 */
public abstract class ConnectionsControllerListener {

    public abstract void userConnectionChanged(String userId, GameConnectionStatus gameConnectionStatus);

}
