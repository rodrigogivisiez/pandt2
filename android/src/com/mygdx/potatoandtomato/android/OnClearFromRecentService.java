package com.mygdx.potatoandtomato.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by SiongLeng on 7/5/2016.
 */
public class OnClearFromRecentService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onTaskRemoved(Intent rootIntent) {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        stopSelf();
    }
}
