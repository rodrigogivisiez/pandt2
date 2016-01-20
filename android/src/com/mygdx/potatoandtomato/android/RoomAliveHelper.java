package com.mygdx.potatoandtomato.android;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import com.mygdx.potatoandtomato.models.PushNotification;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import com.potatoandtomato.common.Status;

/**
 * Created by SiongLeng on 20/1/2016.
 */
public class RoomAliveHelper {

    private Context _context;

    private static RoomAliveHelper _instance;

    public static RoomAliveHelper getInstance() {
        if(_instance == null) _instance = new RoomAliveHelper();
        return _instance;
    }

    private PowerManager.WakeLock _wakeLock;
    private boolean activated;

    public RoomAliveHelper() {
        Broadcaster.getInstance().subscribe(BroadcastEvent.DESTROY_ROOM, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                _instance.dispose();
            }
        });
    }

    public void setContext(Context context) {
        _context = context;

    }

    public boolean isActivated() {
        return activated;
    }

    public void activate(PushNotification pushNotification){
        if(activated) dispose();

        activated = true;

        Intent intent = new Intent(_context, KeepAliveService.class);
        intent.putExtra("title", pushNotification.getTitle());
        intent.putExtra("content", pushNotification.getMessage());
        intent.setAction("START");
        _context.startService(intent);

        PowerManager mgr = (PowerManager) _context.getSystemService(Context.POWER_SERVICE);
        _wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "roomWakeLock");
        _wakeLock.acquire();
    }

    public void dispose(){
        activated = false;

        if(_context != null){
            Intent intent = new Intent(_context, KeepAliveService.class);
            intent.setAction("STOP");
            _context.startService(intent);
        }


        if(_wakeLock != null) {
            _wakeLock.release();
            _wakeLock = null;
        }

    }

}
