package com.mygdx.potatoandtomato.services;

import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.statics.CommonVersion;

/**
 * Created by SiongLeng on 19/1/2016.
 */
public class VersionControl {

    //updating this will not cause external game having outdated client error
    public String getClientVersion(){
        return String.valueOf(Global.CLIENT_VERSION);
    }

    public String getCommonVersion() { return CommonVersion.VERSION; }

}
