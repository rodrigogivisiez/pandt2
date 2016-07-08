package com.potatoandtomato.common.absints;

import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public abstract class DownloaderListener {

    public abstract void onCallback(byte[] bytes, Status st);

    public void onStep(double percentage, long totalSize, long downloadedSize){

    }

}
