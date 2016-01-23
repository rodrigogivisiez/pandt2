package scene_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.gamingkit.MessagingListener;
import com.mygdx.potatoandtomato.helpers.controls.Chat;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.create_game_scene.CreateGameLogic;
import com.mygdx.potatoandtomato.scenes.create_game_scene.CreateGameScene;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import com.potatoandtomato.common.Status;
import helpers.MockModel;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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

        _chat.show();
        _chat.setUserId(MockModel.mockProfile().getUserId());
        _chat.setMessage("testing");
        _chat.sendMessage();
        Room _room = MockModel.mockRoom("1");
        _chat.setRoom(_room);
        _chat.add(new ChatMessage("test", ChatMessage.FromType.USER, MockModel.mockProfile().getUserId()), true);

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
        _chat.setRoom(MockModel.mockRoom("1"));
        _chat.add(new ChatMessage("test", ChatMessage.FromType.USER, MockModel.mockProfile().getUserId()), true);

    }




}
