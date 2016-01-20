package com.mygdx.potatoandtomato.android;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.mygdx.potatoandtomato.absintflis.push_notifications.PushCode;
import com.mygdx.potatoandtomato.models.PushNotification;

public class KeepAliveService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null || intent.getAction() == null){
            RoomAliveHelper.getInstance().dispose();
            return START_NOT_STICKY;
        }

        if (intent.getAction().startsWith("START")) {

            Intent intent2 = new Intent(this, HandleNotificationBroadcastReceiver.class);

            String title = intent.getExtras().getString("title");
            String content = intent.getExtras().getString("content");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                    PushCode.UPDATE_ROOM, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentIntent(pendingIntent)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis());

            builder.setSound(null);
            builder.setVibrate(null);

            Notification n;

            n = builder.build();
            n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

            startForeground(PushCode.UPDATE_ROOM, n);
        } else if (intent.getAction().startsWith("STOP")) {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        RoomAliveHelper.getInstance().dispose();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

}