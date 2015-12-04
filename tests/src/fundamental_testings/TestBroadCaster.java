package fundamental_testings;

import abstracts.TestAbstract;
import com.potatoandtomato.common.BroadcastListener;
import org.junit.Assert;
import org.junit.Test;
import com.potatoandtomato.common.Broadcaster;

/**
 * Created by SiongLeng on 23/9/2015.
 */
public class TestBroadCaster extends TestAbstract {

    @Test
    public void testUnSubscribe(){
        String id = Broadcaster.getInstance().subscribe(-1, new BroadcastListener<MockResponse>() {
            @Override
            public void onCallback(MockResponse obj) {

            }
        });

        Assert.assertEquals(1, Broadcaster.getInstance().getEventCallbacksSize(-1));
        Assert.assertEquals(true, id.length() >= 10);

        Broadcaster.getInstance().unsubscribe(id);
        Assert.assertEquals(0, Broadcaster.getInstance().getEventCallbacksSize(-1));

    }

    @Test
    public void testBroadcast(){
        String id = Broadcaster.getInstance().subscribe(-2, new BroadcastListener<MockResponse>() {
            @Override
            public void onCallback(MockResponse obj) {
                Assert.assertEquals(20, obj.testField);
            }
        });

        Broadcaster.getInstance().broadcast(-2, new MockResponse(20));

    }

    @Test
    public void testSubscribeOnce(){
        String id = Broadcaster.getInstance().subscribeOnce(-3, new BroadcastListener<MockResponse>() {
            @Override
            public void onCallback(MockResponse obj) {
                Assert.assertEquals(10, obj.testField);
                Assert.assertEquals(0, Broadcaster.getInstance().getEventCallbacksSize(-3));
                Assert.assertEquals(0, Broadcaster.getInstance().getSubScribeOnceArr().size());
            }
        });

        Assert.assertEquals(1, Broadcaster.getInstance().getSubScribeOnceArr().size());
        Assert.assertEquals(id, Broadcaster.getInstance().getSubScribeOnceArr().get(0));
        Broadcaster.getInstance().broadcast(-3, new MockResponse(10));

    }

    public class MockResponse{

        public int testField;

        public MockResponse(int _t){
            testField = _t;
        }

    }

}
