package scene_testings;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.gamingkit.MessagingListener;
import com.mygdx.potatoandtomato.services.Chat;
import com.potatoandtomato.common.utils.Threadings;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by SiongLeng on 20/12/2015.
 */
public class TestChat extends TestAbstract{


    @Test
    public void testChat(){

        Services services = T_Services.mockServices();
        final boolean[] waiting = {true};

        services.getGamingKit().addListener(this.getClassTag(), new MessagingListener() {
            @Override
            public void onRoomMessageReceived(ChatMessage chatMessage, String senderId) {
                Assert.assertEquals("testing", chatMessage.getMessage());
                Assert.assertEquals(ChatMessage.FromType.USER, chatMessage.getFromType());
                Assert.assertEquals(MockModel.mockProfile().getUserId(), chatMessage.getSenderId());
                waiting[0] = false;
            }
        });

        Chat _chat = services.getChat();

        Room _room = MockModel.mockRoom("1");
        _chat.showChat();
        _chat.initChat(_room, MockModel.mockProfile().getUserId());
        _chat.sendMessage("testing");

        _chat.newMessage(new ChatMessage("testing", ChatMessage.FromType.USER, MockModel.mockProfile().getUserId(), ""));

        while (waiting[0]){
            Threadings.sleep(1000);
        }

        Assert.assertEquals(1, services.getBroadcaster().getEventCallbacksSize(BroadcastEvent.SCREEN_LAYOUT_CHANGED));

    }


    @Test
    public void testChatMode2(){

        Services services = T_Services.mockServices();
        Chat _chat = services.getChat();
        _chat.setMode(2);
        _chat.initChat(MockModel.mockRoom("1"), MockModel.mockProfile().getUserId());
        _chat.newMessage(new ChatMessage("test", ChatMessage.FromType.USER, MockModel.mockProfile().getUserId(), ""));

    }




}
