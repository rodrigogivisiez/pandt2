package com.mygdx.potatoandtomato.absintflis.downloader;

import com.potatoandtomato.common.Status;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public abstract class DownloaderListener {

    public abstract void onCallback(byte[] bytes, Status st);

    public void onStep(double percentage){

    }

}
