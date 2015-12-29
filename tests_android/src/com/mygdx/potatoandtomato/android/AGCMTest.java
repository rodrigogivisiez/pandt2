package com.mygdx.potatoandtomato.android;

import android.test.ActivityInstrumentationTestCase2;
import com.mygdx.potatoandtomato.helpers.services.GCMSender;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.PushNotification;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import junit.framework.Assert;

/**
 * Created by SiongLeng on 23/12/2015.
 */
public class AGCMTest extends ActivityInstrumentationTestCase2<AndroidLauncher> {

    public AGCMTest() {
        super(AndroidLauncher.class);
    }

    @Override
    protected void setUp() throws Exception {
        getActivity();
    }

    public void testSendAndReceiveGCM() {

        final boolean[] waiting = {true};

        final Profile p = new Profile();

        Broadcaster.getInstance().subscribeOnceWithTimeout(BroadcastEvent.LOGIN_GCM_CALLBACK, 10000, new BroadcastListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                p.setGcmId(obj);
                waiting[0] = false;
            }
        });
        Broadcaster.getInstance().broadcast(BroadcastEvent.LOGIN_GCM_REQUEST);

        while (waiting[0]) {
            Threadings.sleep(100);
        }

        Assert.assertEquals(false, p.getGcmId() == null);

        GCMSender gcmSender = new GCMSender();
        PushNotification push = new PushNotification();
        push.setMessage("testmsg");
        Assert.assertEquals(true, gcmSender.send(p, push));


    }
}
