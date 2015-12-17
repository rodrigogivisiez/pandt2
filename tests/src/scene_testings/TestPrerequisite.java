package scene_testings;

import abstracts.MockGamingKit;
import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.prerequisite_scene.PrerequisiteLogic;
import com.mygdx.potatoandtomato.scenes.prerequisite_scene.PrerequisiteScene;
import helpers.MockModel;
import helpers.T_Services;
import helpers.T_Threadings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class TestPrerequisite extends TestAbstract {

    private Game _game;

    @Before
    public void setUp() throws Exception {
        _game = new Game();
        _game.setVersion("1");
        _game.setAbbr("test_game");
        _game.setAssetUrl("");
        _game.setGameUrl("");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPrerequisiteLogicScene(){
        PrerequisiteLogic logic = new PrerequisiteLogic(mock(PTScreen.class), T_Services.mockServices(), _game, true);
        PrerequisiteScene scene = (PrerequisiteScene) logic.getScene();
        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());
    }

    @Test
    public void testHostGame(){
        Services _services = T_Services.mockServices();
        _services.setGamingKit(new MockGamingKit());

        PTScreen screen = mock(PTScreen.class);
        PrerequisiteLogic logic = Mockito.spy(new PrerequisiteLogic(screen, _services, _game, true));
        logic.init();
        T_Threadings.sleep(10);
        verify(logic, times(0)).joinRoomSuccess();
        verify(logic, times(1)).createRoomSuccess(eq("123"));
        verify(screen, times(1)).toScene(eq(SceneEnum.ROOM), any(Room.class));
    }


    @Test
    public void testJoinGame(){
        Services _services = T_Services.mockServices();
        _services.setGamingKit(new MockGamingKit());

        Room room = MockModel.mockRoom("99");

        PTScreen screen = mock(PTScreen.class);
        PrerequisiteLogic logic = Mockito.spy(new PrerequisiteLogic(screen, _services, _game, false, room));
        logic.init();
        T_Threadings.sleep(10);
        verify(logic, times(1)).joinRoomSuccess();
        verify(logic, times(0)).createRoomSuccess(anyString());
        verify(screen, times(1)).toScene(eq(SceneEnum.ROOM), any(Room.class));
    }


}
