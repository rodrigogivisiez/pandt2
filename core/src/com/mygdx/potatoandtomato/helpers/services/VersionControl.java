package com.mygdx.potatoandtomato.helpers.services;

import com.potatoandtomato.common.CommonVersion;

/**
 * Created by SiongLeng on 19/1/2016.
 */
public class VersionControl {

    //updating this will not cause external game having outdated client error
    public String getClientVersion(){
        return "2";
    }

    public String getCommonVersion() { return CommonVersion.VERSION; }

}
