package com.mygdx.potatoandtomato.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mygdx.potatoandtomato.android.RoomAliveHelper;
import com.mygdx.potatoandtomato.models.PushNotification;
import com.potatoandtomato.common.statics.Vars;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by SiongLeng on 21/1/2016.
 */
public class RoomAliveReceiver extends BroadcastReceiver {

    public RoomAliveReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("KEEP")){
            ObjectMapper mapper = Vars.getObjectMapper();
            try {
                PushNotification pushNotification = mapper.readValue(intent.getExtras().getString("push"), PushNotification.class);
                RoomAliveHelper.activate(context, pushNotification);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else{
            RoomAliveHelper.dispose(context);
        }
    }
}
