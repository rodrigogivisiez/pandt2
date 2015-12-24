package com.mygdx.potatoandtomato.android;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import com.google.android.gms.gcm.GcmListenerService;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import com.mygdx.potatoandtomato.models.PushNotification;

public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;



    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        showNotification(this, new PushNotification(message));
    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body) {
        Context context = getBaseContext();
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
//                .setContentText(body);
//
//        NotificationManager mNotificationManager = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification(Context context, PushNotification pushNotification){

        Intent intent = new Intent(context, AndroidLauncher.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                pushNotification.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(pushNotification.getTitle())
                .setContentText(pushNotification.getMessage())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis());

        if(!pushNotification.isSticky()) builder.setAutoCancel(true);

        if(!pushNotification.isSilentNotification()){
            builder.setDefaults(Notification.DEFAULT_ALL);
        }
        else{
            builder.setSound(null);
            builder.setVibrate(null);
        }

        Notification n;

        n = builder.build();
        if(pushNotification.isSticky()){
            n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(pushNotification.getId(), n);

    }



}