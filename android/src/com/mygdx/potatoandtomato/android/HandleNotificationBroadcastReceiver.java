package com.mygdx.potatoandtomato.android;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;

/**
 * Created by SiongLeng on 5/1/2016.
 */
public class HandleNotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    while( myKM.inKeyguardRestrictedInputMode()) {
                        Thread.sleep(500);
                    }


                    Intent intent = new Intent(context, AndroidLauncher.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // You need this if starting
                    //  the activity from a service
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    context.startActivity(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();







//
//        Intent intent2 = new Intent(context, AndroidLauncher.class);
//        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        context.startActivity(intent);

        //System.out.println("receive");
    }
}
