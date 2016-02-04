package fundamental_testings;

import abstracts.TestAbstract;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Status;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.potatoandtomato.common.Broadcaster;

/**
 * Created by SiongLeng on 23/9/2015.
 */
public class TestBroadCaster extends TestAbstract {

    Broadcaster _broadcaster;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        _broadcaster = new Broadcaster();
    }

    @Test
    public void testUnSubscribe(){
        String id = _broadcaster.subscribe(-1, new BroadcastListener<MockResponse>() {
            @Override
            public void onCallback(MockResponse obj, Status status) {

            }
        });

        Assert.assertEquals(1, _broadcaster.getEventCallbacksSize(-1));
        Assert.assertEquals(true, id.length() >= 10);

        _broadcaster.unsubscribe(id);
        Assert.assertEquals(0, _broadcaster.getEventCallbacksSize(-1));

    }

    @Test
    public void testUnSubscribeAnoynymous(){
        _broadcaster.subscribe(-1, new BroadcastListener<MockResponse>() {
            @Override
            public void onCallback(MockResponse obj, Status status) {
                _broadcaster.unsubscribe(this.getId());
            }
        });

        Assert.assertEquals(1, _broadcaster.getEventCallbacksSize(-1));
        _broadcaster.broadcast(-1, null);
        Assert.assertEquals(0,_broadcaster.getEventCallbacksSize(-1));

    }


    @Test
    public void testBroadcast(){
        String id = _broadcaster.subscribe(-2, new BroadcastListener<MockResponse>() {
            @Override
            public void onCallback(MockResponse obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(20, obj.testField);
            }
        });

        _broadcaster.broadcast(-2, new MockResponse(20));

    }

    @Test
    public void testSubscribeOnce(){
        String id = _broadcaster.subscribeOnce(-3, new BroadcastListener<MockResponse>() {
            @Override
            public void onCallback(MockResponse obj, Status st) {
                Assert.assertEquals(10, obj.testField);
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(0, _broadcaster.getEventCallbacksSize(-3));
                Assert.assertEquals(0, _broadcaster.getSubScribeOnceArr().size());
            }
        });

        Assert.assertEquals(1, _broadcaster.getSubScribeOnceArr().size());
        Assert.assertEquals(id, _broadcaster.getSubScribeOnceArr().get(0));
        _broadcaster.broadcast(-3, new MockResponse(10));

    }

    @Test
    public void testSubscribeOnceWithTimeout(){

        final boolean[] waiting = {true};

        String id = _broadcaster.subscribeOnceWithTimeout(-3, 1000, new BroadcastListener<MockResponse>() {
            @Override
            public void onCallback(MockResponse obj, Status st) {
                Assert.assertEquals(Status.FAILED, st);
                Assert.assertEquals(0, _broadcaster.getEventCallbacksSize(-3));
                Assert.assertEquals(0, _broadcaster.getSubScribeOnceArr().size());
                waiting[0] = false;
            }
        });

        Assert.assertEquals(1, _broadcaster.getSubScribeOnceArr().size());
        Assert.assertEquals(id, _broadcaster.getSubScribeOnceArr().get(0));
        while (waiting[0]){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public class MockResponse{

        public int testField;

        public MockResponse(int _t){
            testField = _t;
        }

    }

}
