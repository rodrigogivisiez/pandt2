package com.mygdx.potatoandtomato.android;

import android.app.Activity;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Libraries.CBLogging;
import com.chartboost.sdk.Model.CBError;
import com.mygdx.potatoandtomato.models.Profile;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 2/6/2016.
 */
public class ChartBoostHelper {

    private Activity mainActivity;
    private final String appId = "574f97cc04b0161bc69b43b1";
    private final String appSignature = "92f17914670ea4eb24f7bb763e0a4a45eb5c9715";
    private Broadcaster broadcaster;

    public ChartBoostHelper(Activity mainActivity, Broadcaster broadcaster) {
        this.mainActivity = mainActivity;
        this.broadcaster = broadcaster;

        Chartboost.startWithAppId(mainActivity, appId, appSignature);
        Chartboost.setLoggingLevel(CBLogging.Level.ALL);
        Chartboost.setDelegate(delegate);
        Chartboost.onCreate(mainActivity);

        Chartboost.cacheRewardedVideo(CBLocation.LOCATION_DEFAULT);
        subscribeBroadcasterEvents();
    }

    public void subscribeBroadcasterEvents(){
        broadcaster.subscribe(BroadcastEvent.USER_READY, new BroadcastListener<Profile>() {
            @Override
            public void onCallback(Profile profile, Status st) {
                Chartboost.setCustomId(profile.getUserId());
            }
        });

        broadcaster.subscribe(BroadcastEvent.HAS_REWARD_VIDEO, new BroadcastListener<RunnableArgs<Boolean>>() {
            @Override
            public void onCallback(RunnableArgs<Boolean> runnable, Status st) {
                runnable.run(Chartboost.hasRewardedVideo(CBLocation.LOCATION_DEFAULT));
            }
        });

        broadcaster.subscribe(BroadcastEvent.SHOW_REWARD_VIDEO, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Chartboost.showRewardedVideo(CBLocation.LOCATION_DEFAULT);
            }
        });
    }

    private ChartboostDelegate delegate = new ChartboostDelegate(){
        @Override
        public boolean shouldRequestInterstitial(String location) {
            return super.shouldRequestInterstitial(location);
        }

        @Override
        public boolean shouldDisplayInterstitial(String location) {
            return super.shouldDisplayInterstitial(location);
        }

        @Override
        public void didCacheInterstitial(String location) {
            super.didCacheInterstitial(location);
        }

        @Override
        public void didFailToLoadInterstitial(String location, CBError.CBImpressionError error) {
            super.didFailToLoadInterstitial(location, error);
        }

        @Override
        public void didDismissInterstitial(String location) {
            super.didDismissInterstitial(location);
        }

        @Override
        public void didCloseInterstitial(String location) {
            super.didCloseInterstitial(location);
        }

        @Override
        public void didClickInterstitial(String location) {
            super.didClickInterstitial(location);
        }

        @Override
        public void didDisplayInterstitial(String location) {
            super.didDisplayInterstitial(location);
        }

        @Override
        public boolean shouldRequestMoreApps(String location) {
            return super.shouldRequestMoreApps(location);
        }

        @Override
        public void didCacheMoreApps(String location) {
            super.didCacheMoreApps(location);
        }

        @Override
        public boolean shouldDisplayMoreApps(String location) {
            return super.shouldDisplayMoreApps(location);
        }

        @Override
        public void didFailToLoadMoreApps(String location, CBError.CBImpressionError error) {
            super.didFailToLoadMoreApps(location, error);
        }

        @Override
        public void didDismissMoreApps(String location) {
            super.didDismissMoreApps(location);
        }

        @Override
        public void didCloseMoreApps(String location) {
            super.didCloseMoreApps(location);
        }

        @Override
        public void didClickMoreApps(String location) {
            super.didClickMoreApps(location);
        }

        @Override
        public void didDisplayMoreApps(String location) {
            super.didDisplayMoreApps(location);
        }

        @Override
        public void didFailToRecordClick(String uri, CBError.CBClickError error) {
            super.didFailToRecordClick(uri, error);
        }

        @Override
        public void didPauseClickForConfirmation(Activity activity) {
            super.didPauseClickForConfirmation(activity);
        }

        @Override
        public boolean shouldDisplayRewardedVideo(String location) {
            return super.shouldDisplayRewardedVideo(location);
        }

        @Override
        public void didCacheRewardedVideo(String location) {
            super.didCacheRewardedVideo(location);
        }

        @Override
        public void didFailToLoadRewardedVideo(String location, CBError.CBImpressionError error) {
            super.didFailToLoadRewardedVideo(location, error);
        }

        @Override
        public void didDismissRewardedVideo(String location) {
            super.didDismissRewardedVideo(location);
        }

        @Override
        public void didCloseRewardedVideo(String location) {
            super.didCloseRewardedVideo(location);
        }

        @Override
        public void didClickRewardedVideo(String location) {
            super.didClickRewardedVideo(location);
        }

        @Override
        public void didCompleteRewardedVideo(String location, int reward) {
            super.didCompleteRewardedVideo(location, reward);
        }

        @Override
        public void didDisplayRewardedVideo(String location) {
            super.didDisplayRewardedVideo(location);
        }

        @Override
        public void willDisplayVideo(String location) {
            super.willDisplayVideo(location);
        }

        @Override
        public void didCacheInPlay(String location) {
            super.didCacheInPlay(location);
        }

        @Override
        public void didFailToLoadInPlay(String location, CBError.CBImpressionError error) {
            super.didFailToLoadInPlay(location, error);
        }

        @Override
        public void didInitialize() {
            super.didInitialize();
        }
    };

    public void onStart() {
        Chartboost.onStart(mainActivity);
    }

    public void onResume() {
        Chartboost.onResume(mainActivity);
    }

    public void onPause() {
        Chartboost.onPause(mainActivity);
    }

    public void onStop() {
        Chartboost.onStop(mainActivity);
    }

    public void onDestroy() {
        Chartboost.onDestroy(mainActivity);
    }

    public boolean onBackPressed() {
        // If an interstitial is on screen, close it.
        return (Chartboost.onBackPressed());
    }

}
