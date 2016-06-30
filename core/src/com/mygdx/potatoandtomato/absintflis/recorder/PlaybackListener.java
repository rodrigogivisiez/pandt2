package com.mygdx.potatoandtomato.absintflis.recorder;

import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 29/6/2016.
 */
public abstract class PlaybackListener {

    private String id;

    public PlaybackListener(String id) {
        this.id = id;
    }

    public abstract void onStartPlay();

    public abstract void onEndPlay(Status status);

    public String getId() {
        return id;
    }
}
