package com.mygdx.potatoandtomato.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by SiongLeng on 3/1/2016.
 */
public class MyApplication extends Application {

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                Intent i = new Intent();
                i.setClass(getApplicationContext(), RoomAliveReceiver.class);
                i.setAction("STOP");
                sendBroadcast(i);
                e.printStackTrace();
                System.exit(1);
            }
        });

    }
}
