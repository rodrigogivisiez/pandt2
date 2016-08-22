package com.mygdx.potatoandtomato.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.mygdx.potatoandtomato.absintflis.analytics.ITracker;
import com.mygdx.potatoandtomato.android.receivers.RoomAliveReceiver;
import com.mygdx.potatoandtomato.statics.Global;
import com.mygdx.potatoandtomato.utils.Logs;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by SiongLeng on 3/1/2016.
 */
public class MyApplication extends Application implements ITracker {
    private boolean analyticEnabled;
    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        if(!analyticEnabled) return;

        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if(!analyticEnabled) return;

        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(
                                    new StandardExceptionParser(this, null)
                                            .getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param action   action of the event
     * @param label    label
     */
    @Override
    public void trackEvent(String action, String label) {
        if(!analyticEnabled) return;

        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory("P_T")
                .setAction(action)
                .setLabel(label)
                .build());
    }


    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        if(!Global.DEBUG){
            AnalyticsTrackers.initialize(this);
            AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
            analyticEnabled = true;
        }

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
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Logs.writeToLog( sw.toString());
                e.printStackTrace();

                System.exit(1);
            }
        });


    }
}
