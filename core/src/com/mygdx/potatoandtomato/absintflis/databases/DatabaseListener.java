package com.mygdx.potatoandtomato.absintflis.databases;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public abstract class DatabaseListener<T> {

    public enum Status{
        SUCCESS, FAILED
    }

    private Class<T> _type;

    private String id;

    public DatabaseListener() {

    }

    public DatabaseListener(Class<T> type) {
        this._type = type;
    }

    public Class<T> getMyType() {
        return this._type;
    }

    public abstract void onCallback(T obj, Status st);

}
