package com.mygdx.potatoandtomato.android.receivers;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mygdx.potatoandtomato.android.AndroidLauncher;

/**
 * Created by SiongLeng on 5/1/2016.
 */
public class HandleNotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent inputIntent) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    int i = 0;
                    KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    while( myKM.inKeyguardRestrictedInputMode()) {
                        i++;
                        Thread.sleep(500);
                    }

                    Thread.sleep(i != 0 ? 700 : 50);

                    Intent intent = new Intent(context, AndroidLauncher.class);

                    if(inputIntent != null && inputIntent.getExtras() != null){
                        intent.putExtras(inputIntent.getExtras());
                    }

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
    }
}
