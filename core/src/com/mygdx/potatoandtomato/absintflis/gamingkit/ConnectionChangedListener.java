package com.mygdx.potatoandtomato.absintflis.gamingkit;

import com.mygdx.potatoandtomato.enums.ClientConnectionStatus;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public abstract class ConnectionChangedListener {


    public ConnectionChangedListener() {

    }

    public abstract void onChanged(String userId, ClientConnectionStatus st);

}
