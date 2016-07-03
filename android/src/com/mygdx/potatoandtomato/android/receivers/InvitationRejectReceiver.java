package com.mygdx.potatoandtomato.android.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.push_notifications.PushCode;
import com.mygdx.potatoandtomato.models.InvitationModel;
import com.mygdx.potatoandtomato.statics.Terms;
import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 3/7/2016.
 */
public class InvitationRejectReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int pushCode = intent.getIntExtra("pushCode", 0);
        if(pushCode == PushCode.SEND_INVITATION){
            String data = intent.getStringExtra("data");
            InvitationModel invitationModel = new InvitationModel(data);

            Firebase.setAndroidContext(context);
            Firebase firebase = new Firebase(Terms.FIREBASE_URL());
            for(String roomId : invitationModel.getPendingInvitationRoomIds()){
                firebase.child("roomInvitations").child(roomId).child(invitationModel.getInvitedUserId()).setValue("0");
            }

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(pushCode);


        }

    }

}
