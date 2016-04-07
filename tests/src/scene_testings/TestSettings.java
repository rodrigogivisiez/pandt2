package scene_testings;

import abstracts.MockDB;
import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.socials.FacebookListener;
import com.mygdx.potatoandtomato.helpers.services.Socials;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.settings_scene.SettingsLogic;
import com.mygdx.potatoandtomato.scenes.settings_scene.SettingsScene;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.enums.Status;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by SiongLeng on 21/12/2015.
 */
public class TestSettings extends TestAbstract {

    @Test
    public void testSettingsSceneLogic(){
        SettingsLogic logic = new SettingsLogic(mock(PTScreen.class), T_Services.mockServices());
        logic.onShow();
        SettingsScene scene = (SettingsScene) logic.getScene();
        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());
    }

    @Test
    public void testSettingsSave(){
        Services services = T_Services.mockServices();
        IDatabase database = Mockito.spy(new MockDB(){
            @Override
            public void getProfileByGameNameLower(String gameName, DatabaseListener<Profile> listener) {
                listener.onCallback(null, Status.SUCCESS);
            }
        });
        services.setDatabase(database);
        SettingsLogic logic = new SettingsLogic(mock(PTScreen.class), services);
        SettingsScene scene = (SettingsScene) logic.getScene();

        scene.getDisplayNameTextField().setText("testing");
        logic.updateProfile();
        Assert.assertEquals("testing", services.getProfile().getDisplayName(15));
        verify(database, times(1)).updateProfile(any(Profile.class), any(DatabaseListener.class));
    }

    @Test
    public void testLogonFacebookLoginRequest(){
        Services services = T_Services.mockServices();

        PTScreen ptScreen = Mockito.mock(PTScreen.class);
        Socials socials = Mockito.spy(new Socials(services.getPreferences(), services.getBroadcaster()));
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((FacebookListener) invocation.getArguments()[0]).onLoginComplete(FacebookListener.Result.SUCCESS);
                return null;
            }
        }).when(socials).loginFacebook(any(FacebookListener.class));
        services.setSocials(socials);
        SettingsLogic logic = new SettingsLogic(mock(PTScreen.class), services);
        logic.setScreen(ptScreen);
        logic.facebookRequest();

        verify(ptScreen, times(1)).backToBoot();

    }

    @Test
    public void testLogonFacebookLogoutRequest(){
        Services services = T_Services.mockServices();
        services.getPreferences().put(Terms.FACEBOOK_USERNAME, "testing");
        services.getPreferences().put(Terms.FACEBOOK_USERID, "123");
        PTScreen ptScreen = Mockito.mock(PTScreen.class);
        Socials socials = Mockito.spy(new Socials(services.getPreferences(), services.getBroadcaster()));
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((FacebookListener) invocation.getArguments()[0]).onLogoutComplete(FacebookListener.Result.SUCCESS);
                return null;
            }
        }).when(socials).loginFacebook(any(FacebookListener.class));
        services.setSocials(socials);
        SettingsLogic logic = new SettingsLogic(mock(PTScreen.class), services);
        logic.setScreen(ptScreen);
        logic.facebookRequest();

        verify(ptScreen, times(1)).backToBoot();
        Assert.assertEquals(null, services.getPreferences().get(Terms.FACEBOOK_USERID));
        Assert.assertEquals(null, services.getPreferences().get(Terms.FACEBOOK_USERNAME));
    }

    @Test
    public void testToggleSounds(){
        Global.ENABLE_SOUND = false;
        Services services = T_Services.mockServices();
        services.getPreferences().put(Terms.SOUNDS_DISABLED, "true");
        SettingsLogic logic = new SettingsLogic(mock(PTScreen.class), services);
        logic.toggleSounds();
        Assert.assertEquals(true, Global.ENABLE_SOUND);
        Assert.assertEquals("false",  services.getPreferences().get(Terms.SOUNDS_DISABLED));
        logic.toggleSounds();
        Assert.assertEquals(false, Global.ENABLE_SOUND);
        Assert.assertEquals("true",  services.getPreferences().get(Terms.SOUNDS_DISABLED));
    }


}
