package com.mygdx.potatoandtomato.android;

import android.test.ActivityInstrumentationTestCase2;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.helpers.utils.Pair;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import com.potatoandtomato.common.Status;
import junit.framework.Assert;

/**
 * Created by SiongLeng on 3/1/2016.
 */
public class ImageLoaderTest extends ActivityInstrumentationTestCase2<AndroidLauncher> {

    public ImageLoaderTest() {
        super(AndroidLauncher.class);
    }

    @Override
    protected void setUp() throws Exception {
        getActivity();
    }


    public void testLoadImageSuccess(){

        final boolean[] waiting = {true};

        Broadcaster.getInstance().subscribe(BroadcastEvent.LOAD_IMAGE_RESPONSE, new BroadcastListener<Pair<String, Texture>>() {
            @Override
            public void onCallback(Pair<String, Texture> obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(false, obj.getFirst() == null);
                Assert.assertEquals(false, obj.getSecond() == null);
                Broadcaster.getInstance().unsubscribe(this.getId());
                waiting[0] = false;
            }
        });

        Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_IMAGE_REQUEST, "http://www.potato-and-tomato.com/sample/icon.png");

        while (waiting[0]){
            Threadings.sleep(1000);
        }

    }

    public void testLoadImageFailed(){

        final boolean[] waiting = {true};

        Broadcaster.getInstance().subscribe(BroadcastEvent.LOAD_IMAGE_RESPONSE, new BroadcastListener<Pair<String, Texture>>() {
            @Override
            public void onCallback(Pair<String, Texture> obj, Status st) {
                Assert.assertEquals(Status.FAILED, st);
                Broadcaster.getInstance().unsubscribe(this.getId());
                waiting[0] = false;
            }
        });

        Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_IMAGE_REQUEST, "http://www.potato-and-tomato.com/sample/nothinghere.png");

        while (waiting[0]){
            Threadings.sleep(1000);
        }

    }

}
