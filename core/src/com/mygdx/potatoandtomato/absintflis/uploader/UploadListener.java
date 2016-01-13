package com.mygdx.potatoandtomato.absintflis.uploader;

import com.potatoandtomato.common.Status;

/**
 * Created by SiongLeng on 11/1/2016.
 */
public abstract class UploadListener<T> {

    public abstract void onCallBack(T result, Status status);

}
