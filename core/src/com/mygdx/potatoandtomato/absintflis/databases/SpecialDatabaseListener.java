package com.mygdx.potatoandtomato.absintflis.databases;

import com.potatoandtomato.common.Status;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public abstract class SpecialDatabaseListener<T, A> {

    private Class _type;


    public SpecialDatabaseListener() {

    }

    public SpecialDatabaseListener(Class type) {
        this._type = type;
    }

    public Class getType() {
        return this._type;
    }

    public abstract void onCallbackTypeOne(T obj, Status st);

    public abstract void onCallbackTypeTwo(A obj, Status st);

}
