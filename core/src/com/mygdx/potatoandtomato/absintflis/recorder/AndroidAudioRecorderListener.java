package com.mygdx.potatoandtomato.absintflis.recorder;

import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 2/7/2016.
 */
public abstract class AndroidAudioRecorderListener {

    public abstract void onStart();

    public abstract void onVolumeChanged();

    public abstract void onFinish(Status status);

}
