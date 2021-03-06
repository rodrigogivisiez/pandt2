package com.mygdx.potatoandtomato.android;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import com.mygdx.potatoandtomato.absintflis.push_notifications.PushCode;
import com.mygdx.potatoandtomato.android.receivers.HandleNotificationBroadcastReceiver;
import com.mygdx.potatoandtomato.android.receivers.QuitGameReceiver;
import com.mygdx.potatoandtomato.android.receivers.RoomAliveReceiver;
import com.mygdx.potatoandtomato.utils.Logs;

public class KeepAliveService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null || intent.getAction() == null){
            stopService();
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
                    .setSmallIcon(R.drawable.ic_stat_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher))
                    .setWhen(0);

            if (Build.VERSION.SDK_INT >= 16) {
                Intent quitIntent = new Intent(this, QuitGameReceiver.class);
                PendingIntent quitPendingIntent = PendingIntent.getBroadcast(
                        this, PushCode.UPDATE_ROOM, quitIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                builder.addAction(R.drawable.ic_stat_action_done, getResources().getString(R.string.back_to_room),
                        pendingIntent);

                builder.addAction(R.drawable.ic_stat_action_settings_power, getResources().getString(R.string.quit),
                        quitPendingIntent);
            }

            builder.setSound(null);
            builder.setVibrate(null);

            Notification n;

            if (Build.VERSION.SDK_INT < 16) {
                n = builder.getNotification();
            } else {
                n = builder.build();
            }

            n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

            startForeground(PushCode.UPDATE_ROOM, n);
        } else if (intent.getAction().startsWith("STOP")) {
            stopService();
        }
        return START_STICKY;
    }

    public void stopService(){
        stopForeground(true);
        stopSelf();
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
        nMgr.cancel(PushCode.UPDATE_ROOM);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopRoomAlive();
        Logs.show("tasks removed");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    private void stopRoomAlive(){
        Intent i = new Intent();
        i.setClass(this, RoomAliveReceiver.class);
        i.setAction("STOP");
        this.sendBroadcast(i);
    }

}