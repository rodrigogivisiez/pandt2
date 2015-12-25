package scene_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.helpers.controls.Chat;
import com.mygdx.potatoandtomato.helpers.services.GCMSender;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.PushNotification;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.invite_scene.InviteLogic;
import helpers.MockModel;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by SiongLeng on 24/12/2015.
 */
public class TestInvite extends TestAbstract {

    @Test
    public void testBootLogicScene(){
        InviteLogic logic = new InviteLogic(mock(PTScreen.class), T_Services.mockServices(), MockModel.mockRoom("1"));
        logic.onInit();
        Assert.assertEquals(true, ((Table) logic.getScene().getRoot()).hasChildren());
    }

    @Test
    public void testToggleUsersAndSendInvitations(){
        Services services = T_Services.mockServices();
        services.setGcmSender(mock(GCMSender.class));
        services.setChat(mock(Chat.class));

        InviteLogic logic = new InviteLogic(mock(PTScreen.class), services, MockModel.mockRoom("1"));
        logic.onInit();
        logic.toggleUserSelection(MockModel.mockProfile("another"));
        Assert.assertEquals(1, logic.getInvitedUsers().size());
        logic.toggleUserSelection(MockModel.mockProfile("another"));
        Assert.assertEquals(0, logic.getInvitedUsers().size());
        logic.toggleUserSelection(MockModel.mockProfile("another"));
        Assert.assertEquals(1, logic.getInvitedUsers().size());

        logic.sendInvitation();
        Assert.assertEquals(true, logic.isAlive());
        logic.dispose();
        Threadings.sleep(100);
        verify(services.getGcmSender(), times(1)).send(any(Profile.class), any(PushNotification.class));
        verify(services.getChat(), times(1)).add(any(ChatMessage.class));
        Threadings.sleep(1000);
        Assert.assertEquals(false, logic.isAlive());
    }



}
