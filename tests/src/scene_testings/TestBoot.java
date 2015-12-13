package scene_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.enums.MascotEnum;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Assets;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.mygdx.potatoandtomato.scenes.boot_scene.BootLogic;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static helpers.T_Assets.mockAssets;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 1/12/2015.
 */
public class TestBoot extends TestAbstract {

    private Assets _assets;

    @Before
    public void setUp() throws Exception {
        _assets = mockAssets();
        _assets.getPreferences().deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        _assets = mockAssets();
        _assets.getPreferences().deleteAll();
    }

    @Test
    public void testBootLogicScene(){
        BootLogic logic = new BootLogic(mock(PTScreen.class), _assets);
        Assert.assertEquals(true, ((Table) logic.getScene().getRoot()).hasChildren());
    }

    @Test
    public void testCreateUser(){
        Assert.assertEquals(true, _assets.getPreferences().get(Terms.USERID) == null);
        Assert.assertEquals(true, _assets.getProfile().getUserId() == null);
        BootLogic logic = new BootLogic(mock(PTScreen.class), _assets);
        logic.showLoginBox();
        logic.loginPT();
        Assert.assertEquals(false, _assets.getPreferences().get(Terms.USERID) == null);
        Assert.assertEquals(false, _assets.getProfile().getUserId() == null);
    }

    @Test
    public void testLoginWithExistingUser(){
        _assets.getPreferences().put(Terms.USERID, "999");
        BootLogic logic = new BootLogic(mock(PTScreen.class), _assets);
        logic.showLoginBox();
        logic.loginPT();
        Assert.assertEquals("999", _assets.getProfile().getUserId());
    }

    @Test
    public void testRetrieveUserFailed(){
        BootLogic logic = new BootLogic(mock(PTScreen.class), _assets);
        logic.showLoginBox();
        logic.retrieveUserFailed();
    }

    @Test
    public void testLoginSuccessUpdateFbId(){

        IDatabase mockDatabase = mock(IDatabase.class);
        _assets.setDatabase(mockDatabase);
        final boolean[] called = {false};
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                if(((Profile) arguments[0]).getFacebookUserId().equals("999")){
                    called[0] = true;
                }
                return null;
            }
        }).when(mockDatabase).updateProfile(any(Profile.class));
        _assets.getPreferences().put(Terms.FACEBOOK_USERID, "999");
        BootLogic logic = new BootLogic(mock(PTScreen.class), _assets);
        logic.loginPTSuccess();
        Assert.assertEquals("999", _assets.getProfile().getFacebookUserId());
        Assert.assertEquals(true, called[0]);
    }

    @Test
    public void testLoginSuccessWithMascot(){

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

        BootLogic logic = new BootLogic(mockPTScreen, _assets);
        _assets.getProfile().setMascotEnum(MascotEnum.TOMATO);
        logic.loginPTSuccess();
        Assert.assertEquals(true, called[0]);
    }

    @Test
    public void testLoginSuccessWithoutMascot(){

        final boolean[] called = {false};
        PTScreen mockPTScreen = mock(PTScreen.class);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                called[0] = true;
                return null;
            }
        }).when(mockPTScreen).toScene(SceneEnum.MASCOT_PICK);

        BootLogic logic = new BootLogic(mockPTScreen, _assets);
        logic.loginPTSuccess();
        Assert.assertEquals(true, called[0]);
    }


}
