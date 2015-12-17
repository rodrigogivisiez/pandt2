package connection_testings;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.gamingkit.JoinRoomListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.helpers.services.Appwarp;
import helpers.T_Threadings;
import org.junit.*;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class TestAppwarp extends TestAbstract {

    private GamingKit _gamingKit;

    @Before
    public void setUp() throws Exception {
        if(_gamingKit == null){
            final boolean[] waiting = {true};
            _gamingKit = new Appwarp();

            _gamingKit.addListener(new ConnectionChangedListener() {
                @Override
                public void onChanged(Status st) {
                    waiting[0] = false;
                    Assert.assertEquals(Status.CONNECTED, st);
                }
            });

            _gamingKit.connect("random");

            while(waiting[0]){
                T_Threadings.sleep(100);
            }
        }
    }

    @Test
    public void testCreateRoomAndUpdatePeers(){
        final boolean[] waiting = {true};
        final boolean[] success = {false};
        final String[] joinedRoomId = new String[1];

        _gamingKit.addListener(new JoinRoomListener() {
            @Override
            public void onRoomJoined(String roomId) {
                waiting[0] = false;
                Assert.assertEquals(false, roomId == null);
                joinedRoomId[0] = roomId;
                success[0] = true;
            }

            @Override
            public void onJoinRoomFailed() {
                waiting[0] = false;
                success[0] = false;
            }
        });

        _gamingKit.createAndJoinRoom();

        while(waiting[0]){
            T_Threadings.sleep(100);
        }

        Assert.assertEquals(true, success[0]);

        waiting[0] = true;
        final int code = 10;
        final String sendingMsg = "nothing";

        //test update peers
        _gamingKit.addListener(new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int broadcastCode, String msg, String senderId) {
                Assert.assertEquals(code, broadcastCode);
                Assert.assertEquals(sendingMsg, msg);
                Assert.assertEquals(senderId, "random");
                waiting[0] = false;
            }
        });

        _gamingKit.updateRoomMates(code, sendingMsg);

        while(waiting[0]){
            T_Threadings.sleep(100);
        }

    }


}
