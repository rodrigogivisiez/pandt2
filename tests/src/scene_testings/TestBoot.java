package scene_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.helpers.utils.Assets;
import com.mygdx.potatoandtomato.scenes.boot_scene.BootLogic;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static helpers.T_Assets.mockAssets;
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
        Assert.assertEquals(true, _assets.getPreferences().getUserId() == null);
        Assert.assertEquals(true, _assets.getProfile().getUserId() == null);
        BootLogic logic = new BootLogic(mock(PTScreen.class), _assets);
        logic.showLoginBox();
        logic.loginPT();
        Assert.assertEquals(false, _assets.getPreferences().getUserId() == null);
        Assert.assertEquals(false, _assets.getProfile().getUserId() == null);
    }

    @Test
    public void testLoginWithExistingUser(){
        _assets.getPreferences().storeUserId("999");
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

}
