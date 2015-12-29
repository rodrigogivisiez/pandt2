package com.mygdx.potatoandtomato.helpers.services;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.PushNotification;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by SiongLeng on 23/12/2015.
 */
public class GCMSender {

    private final String _apiKey = "AIzaSyAsjcqh5WJFtwEOYPqrddtMjZZgEIpEZAQ";

    public boolean send(ArrayList<Profile> profiles, PushNotification msg){

        final ArrayList<String> regIds = new ArrayList();
        for(Profile p : profiles){
            if(p.getGcmId() != null) regIds.add(p.getGcmId());
        }

        if(regIds.size() > 0){
            final Sender sender = new Sender(_apiKey);
            final Message message = new Message.Builder()
                    .addData("message", msg.toString())
                    .build();

            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    try {
                        MulticastResult result = sender.send(message, regIds, 5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return true;
        }
        return false;
    }

    public boolean send(Profile profile, PushNotification msg){
        ArrayList<Profile> profiles = new ArrayList();
        profiles.add(profile);
        return send(profiles, msg);
    }




}






