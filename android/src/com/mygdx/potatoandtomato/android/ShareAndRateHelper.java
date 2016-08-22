package com.mygdx.potatoandtomato.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import com.mygdx.potatoandtomato.services.Texts;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;

import java.util.List;

/**
 * Created by SiongLeng on 11/8/2016.
 */
public class ShareAndRateHelper {

    private Broadcaster broadcaster;
    private AndroidLauncher androidLauncher;
    private Texts texts;

    public ShareAndRateHelper(Broadcaster broadcaster, Texts texts, AndroidLauncher androidLauncher) {
        this.broadcaster = broadcaster;
        this.androidLauncher = androidLauncher;
        this.texts = texts;
        subscribe();
    }

    private void subscribe(){
        broadcaster.subscribe(BroadcastEvent.SHARE_P_AND_T, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = texts.shareBody();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, texts.shareSubject());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                androidLauncher.startActivity(Intent.createChooser(sharingIntent, texts.shareDialogTitle()));
            }
        });

        broadcaster.subscribe(BroadcastEvent.OPEN_P_AND_T_AT_STORE, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                openAppRating(androidLauncher);
            }
        });

    }


    public void openAppRating(Context context) {
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+context.getPackageName()));
            context.startActivity(webIntent);
        }
    }

}
