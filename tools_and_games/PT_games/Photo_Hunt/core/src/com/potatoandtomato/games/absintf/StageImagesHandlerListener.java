package com.potatoandtomato.games.absintf;

/**
 * Created by SiongLeng on 18/4/2016.
 */
public abstract class StageImagesHandlerListener {

    public abstract void onTouch(float x, float y);

    public abstract void requestCircleAll();

    public abstract void cancelCircleAll();
}
