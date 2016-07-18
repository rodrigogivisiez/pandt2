package com.mygdx.potatoandtomato.absintflis.cachings;

import com.badlogic.gdx.utils.Disposable;

/**
 * Created by SiongLeng on 18/7/2016.
 */
public abstract class CacheListener<T> implements Disposable {

    private boolean disposed;

    public abstract void onResult(T result);


    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public void dispose() {
        disposed = true;
    }
}
