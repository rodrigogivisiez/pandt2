package com.mygdx.potatoandtomato.android;

import android.content.Context;
import android.os.PowerManager;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;

/**
 * Created by SiongLeng on 5/1/2016.
 */
public class KeepAlive {

    private Context _contenxt;
    PowerManager.WakeLock _wakeLock;

    public KeepAlive(Context _contenxt) {
        this._contenxt = _contenxt;

        Broadcaster.getInstance().subscribe(BroadcastEvent.KEEP_APPS_ALIVE, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                keep();
            }
        });

        Broadcaster.getInstance().subscribe(BroadcastEvent.REMOVE_APPS_ALIVE, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                release();
            }
        });

    }

    public void keep(){
        PowerManager mgr = (PowerManager) _contenxt.getSystemService(Context.POWER_SERVICE);
        _wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "roomWakeLock");
        _wakeLock.acquire();
    }

    public void release(){
        if(_wakeLock != null) _wakeLock.release();
    }

}
