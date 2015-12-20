package connection_testings;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.gamingkit.*;
import com.mygdx.potatoandtomato.helpers.services.Appwarp;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.ChatMessage;
import helpers.MockModel;
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

            _gamingKit.connect(MockModel.mockProfile("random"));

            while(waiting[0]){
                T_Threadings.sleep(100);
            }
        }
    }

    @Test
    public void testCreateRoomAndUpdatePeersAndPrivateMsgAndLeaveRoom(){
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
        final int[] monitorCount = new int[]{0};

        //test update peers
        _gamingKit.addListener(new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int broadcastCode, String msg, String senderId) {
                Assert.assertEquals(code, broadcastCode);
                Assert.assertEquals(sendingMsg, msg);
                Assert.assertEquals(senderId, "random");
                waiting[0] = false;
                monitorCount[0]++;
            }
        });

        _gamingKit.updateRoomMates(code, sendingMsg);

        while(waiting[0]){
            T_Threadings.sleep(100);
        }

        waiting[0] = true;
        final ChatMessage chatMessage = new ChatMessage("test msg", ChatMessage.FromType.USER, "random");
        //test send room msg
        _gamingKit.addListener(new MessagingListener() {
            @Override
            public void onRoomMessageReceived(String msg, String senderId) {
                Assert.assertEquals(chatMessage.getMessage(), msg);
                Assert.assertEquals(chatMessage.getSenderId(), senderId);
                waiting[0] = false;
            }
        });

        _gamingKit.sendRoomMessage(chatMessage.getMessage());

        while(waiting[0]){
            T_Threadings.sleep(100);
        }

        _gamingKit.leaveRoom();

        _gamingKit.updateRoomMates(code, sendingMsg);

        Threadings.sleep(1000);

        Assert.assertEquals(1, monitorCount[0]);

    }


}
