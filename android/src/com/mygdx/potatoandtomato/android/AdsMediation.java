package com.mygdx.potatoandtomato.android;


import android.content.Intent;
import com.aerserv.sdk.*;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.Libraries.CBLogging;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.RunnableArgs;

import java.util.List;

/**
 * Created by SiongLeng on 22/8/2016.
 */
public class AdsMediation {

    private AdsMediation _this;
    private AndroidLauncher androidLauncher;
    private Broadcaster broadcaster;
    private boolean hasAds;
    private AerServInterstitial interstitial;

    public AdsMediation(AndroidLauncher androidLauncher, Broadcaster broadcaster) {
        this.androidLauncher = androidLauncher;
        this.broadcaster = broadcaster;
        _this = this;

        AerServSdk.init(androidLauncher, "1001257");
        showRewardedVideo(false, null);
        subscribe();



    }

    private void subscribe(){
        broadcaster.subscribe(BroadcastEvent.HAS_REWARD_VIDEO, new BroadcastListener<RunnableArgs<Boolean>>() {
            @Override
            public void onCallback(RunnableArgs<Boolean> inRunnable, Status st) {
                inRunnable.run(hasAds);
            }
        });

        broadcaster.subscribe(BroadcastEvent.SHOW_REWARD_VIDEO, new BroadcastListener<RunnableArgs<Boolean>>() {
            @Override
            public void onCallback(RunnableArgs<Boolean> runnable, Status st) {
                showRewardedVideo(true, runnable);
            }
        });
    }


    public void onCreate() {
    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }

    public boolean onBackPressed() {
        // If ad is on the screen - close it
        return false;
    }

    public void showRewardedVideo(final boolean showAds, final RunnableArgs<Boolean> runnable) {
        AerServEventListener listener = new AerServEventListener() {
            @Override
            public void onAerServEvent(AerServEvent event, List<Object> args) {
                Logs.show(event.name());
                switch (event) {
                    case PRELOAD_READY:
                        hasAds = true;
                        if(showAds) interstitial.show();
                        break;

                    case AD_IMPRESSION:
                        if(runnable != null) runnable.run(true);
                        break;

                    case AD_FAILED:
                        hasAds = false;
                        if(runnable != null) runnable.run(false);
                        break;

                    case VC_REWARDED:
                        broadcaster.broadcast(BroadcastEvent.WATCHED_VIDEO_ADS);
                        break;
                }
            }
        };

        AerServConfig config = new AerServConfig(androidLauncher, "1015652")
                .setPreload(true)
                .setEventListener(listener);

        interstitial = new AerServInterstitial(config);
    }

}
