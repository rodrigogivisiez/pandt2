package com.mygdx.potatoandtomato.android.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.badlogic.gdx.Gdx;
import com.firebase.client.Firebase;
import com.mygdx.potatoandtomato.absintflis.push_notifications.PushCode;
import com.mygdx.potatoandtomato.models.InvitationModel;
import com.mygdx.potatoandtomato.statics.Terms;
import com.shephertz.app42.gaming.multiplayer.client.ConnectionState;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

/**
 * Created by SiongLeng on 6/8/2016.
 */
public class QuitGameReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(PushCode.UPDATE_ROOM);
        Gdx.app.exit();
    }

}
