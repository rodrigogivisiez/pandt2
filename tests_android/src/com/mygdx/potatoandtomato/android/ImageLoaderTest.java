package com.mygdx.potatoandtomato.android;

import android.test.ActivityInstrumentationTestCase2;
import com.badlogic.gdx.graphics.Texture;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import junit.framework.Assert;

/**
 * Created by SiongLeng on 3/1/2016.
 */
public class ImageLoaderTest extends ActivityInstrumentationTestCase2<AndroidLauncher> {

    private Broadcaster broadcaster;

    public ImageLoaderTest() {
        super(AndroidLauncher.class);
    }

    @Override
    protected void setUp() throws Exception {
        AndroidLauncher launcher = getActivity();
        broadcaster = launcher.getBroadcaster();
    }


    public void testLoadImageSuccess(){

        final boolean[] waiting = {true};

        broadcaster.subscribe(BroadcastEvent.LOAD_IMAGE_RESPONSE, new BroadcastListener<Pair<String, Texture>>() {
            @Override
            public void onCallback(Pair<String, Texture> obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(false, obj.getFirst() == null);
                Assert.assertEquals(false, obj.getSecond() == null);
                broadcaster.unsubscribe(this.getId());
                waiting[0] = false;
            }
        });

        broadcaster.broadcast(BroadcastEvent.LOAD_IMAGE_REQUEST, "http://www.potato-and-tomato.com/sample/icon.png");

        while (waiting[0]){
            Threadings.sleep(1000);
        }

    }

    public void testLoadImageFailed(){
        final boolean[] waiting = {true};

        broadcaster.subscribe(BroadcastEvent.LOAD_IMAGE_RESPONSE, new BroadcastListener<Pair<String, Texture>>() {
            @Override
            public void onCallback(Pair<String, Texture> obj, Status st) {
                Assert.assertEquals(Status.FAILED, st);
                broadcaster.unsubscribe(this.getId());
                waiting[0] = false;
            }
        });

        broadcaster.broadcast(BroadcastEvent.LOAD_IMAGE_REQUEST, "http://www.potato-and-tomato.com/sample/nothinghere.png");

        while (waiting[0]){
            Threadings.sleep(1000);
        }

    }

}
