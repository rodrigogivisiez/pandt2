package scene_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.scenes.game_sandbox_scene.GameSandboxLogic;
import com.mygdx.potatoandtomato.scenes.game_sandbox_scene.GameSandboxScene;
import helpers.MockModel;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 6/1/2016.
 */
public class TestGameSandBox extends TestAbstract{

    @Test
    public void testGameSandBoxLogicScene(){
        Room _room = MockModel.mockRoom("123");
        GameSandboxLogic logic = new GameSandboxLogic(mock(PTScreen.class), T_Services.mockServices(), _room);
        GameSandboxScene scene = (GameSandboxScene) logic.getScene();
        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());
    }
}
