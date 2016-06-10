package com.potatoandtomato.common.absints;

import com.potatoandtomato.common.enums.SelfConnectionStatus;

/**
 * Created by SiongLeng on 27/5/2016.
 */
public abstract class SelfConnectionListener {

    public abstract void onSelfConnectionChanged(SelfConnectionStatus connectionStatus);

}


