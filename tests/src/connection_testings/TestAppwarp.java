package connection_testings;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.gamingkit.*;
import com.mygdx.potatoandtomato.services.Appwarp;
import com.potatoandtomato.common.utils.Threadings;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;
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

            _gamingKit.addListener(getClassTag(), new ConnectionChangedListener() {
                @Override
                public void onChanged(ConnectStatus st) {
                    waiting[0] = false;
                    Assert.assertEquals(ConnectStatus.CONNECTED, st);
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

        _gamingKit.addListener(getClassTag(), new JoinRoomListener() {
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
        final String sendingMsg = "supersuper";
        final int[] monitorCount = new int[]{0};

        String finalMsg = "";
        for(int i=0; i<400; i++){
            finalMsg += sendingMsg;
        }

        //test update peers
        final String finalMsg1 = finalMsg;
        _gamingKit.addListener(getClassTag(), new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int broadcastCode, String msg, String senderId) {
                Assert.assertEquals(code, broadcastCode);
                Assert.assertEquals(finalMsg1, msg);
                Assert.assertEquals(senderId, "random");
                waiting[0] = false;
                monitorCount[0]++;
            }

            @Override
            public void onUpdateRoomMatesReceived(byte identifier, byte[] data, String senderId) {

            }
        });

        _gamingKit.updateRoomMates(code, finalMsg);

        while(waiting[0]){
            T_Threadings.sleep(100);
        }

        //test update peers by bytes
        waiting[0] = true;
        final byte[] result = new byte[2000];
        for(int i = 0; i < result.length; i++){
            result[i] = 99;
        }
        _gamingKit.addListener(getClassTag(), new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int broadcastCode, String msg, String senderId) {

            }

            @Override
            public void onUpdateRoomMatesReceived(byte identifier, byte[] data, String senderId) {
                Assert.assertEquals(1, identifier);
                for(int i = 0; i < data.length; i++){
                    Assert.assertEquals(99, data[i]);
                }
                Assert.assertEquals(senderId, "random");
                Assert.assertEquals(result.length, data.length);
                waiting[0] = false;
                monitorCount[0]++;
            }
        });

        _gamingKit.updateRoomMates((byte) 1, result);

        while(waiting[0]){
            T_Threadings.sleep(100);
        }

        waiting[0] = true;
        final ChatMessage chatMessage = new ChatMessage("test msg", ChatMessage.FromType.USER, "random");
        //test send room msg
        _gamingKit.addListener(getClassTag(), new MessagingListener() {
            @Override
            public void onRoomMessageReceived(ChatMessage result, String senderId) {
                Assert.assertEquals(chatMessage.getMessage(), result.getMessage());
                Assert.assertEquals(chatMessage.getSenderId(), senderId);
                waiting[0] = false;
            }
        });

        _gamingKit.sendRoomMessage(chatMessage);

        while(waiting[0]){
            T_Threadings.sleep(100);
        }

        _gamingKit.leaveRoom();

        _gamingKit.updateRoomMates(code, sendingMsg);

        Threadings.sleep(1000);

        Assert.assertEquals(2, monitorCount[0]);

    }


}
