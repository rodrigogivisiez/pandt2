package com.mygdx.potatoandtomato.absintflis.cachings;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.enums.DataCacheRefreshMethod;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 18/7/2016.
 */
public abstract class CacheAbstract<T> implements Disposable {
    protected T cached;
    private SafeThread refreshThread;
    protected boolean disposed;
    private int refreshAtValue;
    private int currentRefreshValue;
    private DataCacheRefreshMethod dataCacheRefreshMethod;

    public CacheAbstract() {
        dataCacheRefreshMethod = DataCacheRefreshMethod.Manual;
    }

    public void getData(final CacheListener<T> cacheListener){
        if(dataCacheRefreshMethod == DataCacheRefreshMethod.Count){
            currentRefreshValue++;
            if(currentRefreshValue > refreshAtValue){
                refreshCache();
                currentRefreshValue = 0;
            }
        }

        if(cached != null){
            cacheListener.onResult(cached);
        }
        else{
            refreshCache();
            if(cacheListener == null) return;
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    while (cached == null){
                        Threadings.sleep(300);
                        if(disposed || cacheListener.isDisposed()) return;
                    }
                    cacheListener.onResult(cached);
                }
            });
        }
    }


    public void refreshCache(){
        if(refreshThread != null && !refreshThread.isKilled()) return;
        else{
            final SafeThread safeThread = new SafeThread();
            refreshThread = safeThread;
            retrieveDataRemote(new CacheListener<T>() {
                @Override
                public void onResult(T result) {
                    if(!safeThread.isKilled()){
                        cached = result;
                        safeThread.kill();
                        currentRefreshValue = 0;
                    }
                }
            });
        }

    }

    public abstract void retrieveDataRemote(CacheListener<T> cacheListener);

    public void useCountRefreshMethod(int refreshAtCount){
        dataCacheRefreshMethod = DataCacheRefreshMethod.Count;
        refreshAtValue = refreshAtCount;
    }

    public void useTimeRefreshMethod(int refreshAtSecs){
        dataCacheRefreshMethod = DataCacheRefreshMethod.Time;
        refreshAtValue = refreshAtSecs;

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                currentRefreshValue = 0;
                while (true){
                    if(disposed) return;
                    else{
                        if(currentRefreshValue > refreshAtValue){
                            refreshCache();
                            currentRefreshValue = 0;
                        }
                        currentRefreshValue++;
                        Threadings.sleep(1000);
                    }
                }

            }
        });

    }

    public void resetCache(){
        clearCache();
        currentRefreshValue = 0;
        refreshCache();
    }

    public void clearCache(){
        cached = null;
        if(refreshThread != null) refreshThread.kill();
    }

    @Override
    public void dispose() {
        disposed = true;
        clearCache();
    }
}
