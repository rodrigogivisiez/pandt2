package com.mygdx.potatoandtomato.android;

import android.content.Intent;
import com.mygdx.potatoandtomato.services.Texts;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 11/8/2016.
 */
public class ShareHelper {

    private Broadcaster broadcaster;
    private AndroidLauncher androidLauncher;
    private Texts texts;

    public ShareHelper(Broadcaster broadcaster, Texts texts, AndroidLauncher androidLauncher) {
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
    }


}
