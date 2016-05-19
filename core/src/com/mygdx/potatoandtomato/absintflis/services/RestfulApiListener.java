package com.mygdx.potatoandtomato.absintflis.services;

import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 13/5/2016.
 */
public abstract class RestfulApiListener<T> {

    public abstract void onCallback(T obj, Status st);

}
