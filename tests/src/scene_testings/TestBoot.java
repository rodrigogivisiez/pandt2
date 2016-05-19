package scene_testings;

import abstracts.MockGamingKit;
import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.potatoandtomato.common.utils.Threadings;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.statics.Terms;
import com.mygdx.potatoandtomato.scenes.boot_scene.BootLogic;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.enums.Status;
import helpers.T_Services;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static helpers.T_Services.mockServices;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 1/12/2015.
 */
public class TestBoot extends TestAbstract {

    private Services _services;

    @Before
    public void setUp() throws Exception {
        _services = T_Services.mockServices();
        _services.getPreferences().deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        _services = T_Services.mockServices();
        _services.getPreferences().deleteAll();
    }

    @Test
    public void testBootLogicScene(){
        BootLogic logic = new BootLogic(mock(PTScreen.class), _services);
        logic.onShow();
        Assert.assertEquals(true, ((Table) logic.getScene().getRoot()).hasChildren());
    }

    @Test
    public void testCreateUser(){
        _services.getProfile().setUserId(null);
        Assert.assertEquals(true, _services.getSocials().getFacebookProfile() == null);
        Assert.assertEquals(true, _services.getProfile().getUserId() == null);
        BootLogic logic = new BootLogic(mock(PTScreen.class), _services);
        logic.onShow();
        logic.showLoginBox();
        logic.afterFacebookPhase();
        Assert.assertEquals(true, _services.getSocials().getFacebookProfile() == null);
        Assert.assertEquals(false, _services.getProfile().getUserId() == null);
    }

    @Test
    public void testLoginWithExistingUser(){
        _services.getPreferences().put(Terms.USERID, "999");
        BootLogic logic = new BootLogic(mock(PTScreen.class), _services);
        logic.onShow();
        logic.showLoginBox();
        logic.retrieveUserToken();
        Assert.assertEquals("999", _services.getProfile().getUserId());
    }

    @Test
    public void testRetrieveUserFailed(){
        BootLogic logic = new BootLogic(mock(PTScreen.class), _services);
        logic.onShow();
        logic.showLoginBox();
        logic.retrieveUserFailed();
    }

    @Test
    public void testUpdateGCMId(){
        _services.getBroadcaster().subscribe(BroadcastEvent.LOGIN_GCM_REQUEST, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                _services.getBroadcaster().broadcast(BroadcastEvent.LOGIN_GCM_CALLBACK, "gcmid1", Status.SUCCESS);
            }
        });
        BootLogic logic = new BootLogic(mock(PTScreen.class), _services);
        logic.loginGCM();
        Assert.assertEquals("gcmid1", _services.getProfile().getGcmId());
    }


    @Test
    public void testLoginSuccessWithGameName(){

        final boolean[] called = {false};
        PTScreen mockPTScreen = mock(PTScreen.class);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                called[0] = true;
                return null;
            }
        }).when(mockPTScreen).toScene(SceneEnum.GAME_LIST);

        _services.setGamingKit(new MockGamingKit(){
            @Override
            public void connect(Profile user) {
                super.connect(user);
                this.onConnectionChanged(true);
            }
        });
        BootLogic logic = new BootLogic(mockPTScreen, _services);

        logic.onShow();
        _services.getProfile().setGameName("abc");
        logic.loginPTSuccess();

        Threadings.sleep(100);

        Assert.assertEquals(true, called[0]);
    }

    @Test
    public void testLoginSuccessWithoutGameName(){

        final boolean[] called = {false};
        PTScreen mockPTScreen = mock(PTScreen.class);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                called[0] = true;
                return null;
            }
        }).when(mockPTScreen).toScene(SceneEnum.INPUT_NAME);

        _services.setGamingKit(new MockGamingKit(){
            @Override
            public void connect(Profile user) {
                super.connect(user);
                this.onConnectionChanged(true);
            }
        });

        BootLogic logic = new BootLogic(mockPTScreen, _services);
        logic.onShow();
        _services.getProfile().setGameName(null);
        logic.loginPTSuccess();

        Threadings.sleep(100);
        Assert.assertEquals(true, called[0]);
    }


}
