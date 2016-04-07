package com.mygdx.potatoandtomato.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import com.mygdx.potatoandtomato.models.PushNotification;

/**
 * Created by SiongLeng on 20/1/2016.
 */
public class RoomAliveHelper {


    private static PowerManager.WakeLock _wakeLock;
    private static boolean activated;


    public static boolean isActivated() {
        return activated;
    }

    public static void setActivated(boolean activated) {
        RoomAliveHelper.activated = activated;
    }

    public static void activate(Context context, PushNotification pushNotification){
        if(activated) dispose(context);

        activated = true;

        Intent intent = new Intent(context, KeepAliveService.class);
        intent.putExtra("title", pushNotification.getTitle());
        intent.putExtra("content", pushNotification.getMessage());
        intent.setAction("START");
        context.startService(intent);

        PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        _wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "roomWakeLock");
        _wakeLock.acquire();

    }

    public static void dispose(Context context){
        activated = false;

        if(context != null){
            Intent intent = new Intent(context, KeepAliveService.class);
            intent.setAction("STOP");
            context.startService(intent);
        }

        if(_wakeLock != null) {
            if(_wakeLock.isHeld()) _wakeLock.release();
            _wakeLock = null;
        }

    }

    public static void save(Bundle outState){
        outState.putBoolean("roomAliveActivated", isActivated());
    }

    public static void restore(Bundle savedInstanceState){
        if(savedInstanceState.containsKey("roomAliveActivated")){
            setActivated(savedInstanceState.getBoolean("roomAliveActivated"));
        }
    }

}
