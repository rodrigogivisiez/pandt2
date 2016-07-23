package com.mygdx.potatoandtomato.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import com.google.android.gms.gcm.GcmListenerService;
import com.mygdx.potatoandtomato.absintflis.push_notifications.PushCode;
import com.mygdx.potatoandtomato.android.receivers.HandleNotificationBroadcastReceiver;
import com.mygdx.potatoandtomato.android.receivers.InvitationAcceptReceiver;
import com.mygdx.potatoandtomato.android.receivers.InvitationRejectReceiver;
import com.mygdx.potatoandtomato.android.receivers.RoomAliveReceiver;
import com.mygdx.potatoandtomato.models.PushNotification;
import com.potatoandtomato.common.statics.Vars;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;
    public static GcmMessageHandler _me;
    PowerManager.WakeLock _wakeLock;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        PushNotification pushNotification = new PushNotification(message);
        handleNotification(this, pushNotification);
    }

    public static void handleNotification(Context context, PushNotification pushNotification){
        if(pushNotification.getId() == PushCode.UPDATE_ROOM && !RoomAliveHelper.isActivated()){
            try {
                Intent i = new Intent();
                i.setClass(context, RoomAliveReceiver.class);
                i.setAction("KEEP");
                ObjectMapper mapper = Vars.getObjectMapper();
                i.putExtra("push", mapper.writeValueAsString(pushNotification));
                context.sendBroadcast(i);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        else{
            showNotification(context,pushNotification);
        }
    }

    private static void showNotification(Context context, PushNotification pushNotification){
        Intent intent = new Intent(context, HandleNotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                pushNotification.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(pushNotification.getTitle())
                .setContentText(pushNotification.getMessage())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis());

        if(pushNotification.getId() == PushCode.SEND_INVITATION){
            Intent acceptIntent = new Intent(context, InvitationAcceptReceiver.class);
            acceptIntent.putExtra("pushCode", PushCode.SEND_INVITATION);
            acceptIntent.putExtra("data", pushNotification.getExtras());
            PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(context,
                    pushNotification.getId(), acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent rejectIntent = new Intent(context, InvitationRejectReceiver.class);
            rejectIntent.putExtra("pushCode", PushCode.SEND_INVITATION);
            rejectIntent.putExtra("data", pushNotification.getExtras());
            PendingIntent rejectPendingIntent = PendingIntent.getBroadcast(context,
                    pushNotification.getId(), rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.addAction(R.drawable.ic_stat_action_done, context.getResources().getString(R.string.invitation_accept),
                    acceptPendingIntent);
            builder.addAction(R.drawable.ic_stat_navigation_close, context.getResources().getString(R.string.invitation_reject),
                    rejectPendingIntent);
        }


        if(!pushNotification.isSticky()) builder.setAutoCancel(true);

        if(pushNotification.isSilentNotification() || (pushNotification.isSilentIfInGame() && AndroidLauncher.isVisible())){
            builder.setSound(null);
            builder.setVibrate(null);
        }
        else{
            builder.setDefaults(Notification.DEFAULT_ALL);
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