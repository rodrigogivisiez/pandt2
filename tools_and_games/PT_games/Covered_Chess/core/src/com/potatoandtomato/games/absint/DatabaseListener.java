package com.potatoandtomato.games.absint;


import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public abstract class DatabaseListener<T> {

    private Class _type;

    public DatabaseListener() {

    }

    public DatabaseListener(Class type) {
        this._type = type;
    }

    public Class getType() {
        return this._type;
    }

    public abstract void onCallback(T obj, Status st);

}
