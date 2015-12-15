package scene_testings;

import abstracts.MockDB;
import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.scenes.create_game_scene.CreateGameLogic;
import com.mygdx.potatoandtomato.scenes.create_game_scene.CreateGameScene;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 14/12/2015.
 */
public class TestCreateGame extends TestAbstract {

    @Test
    public void testCreateGameLogicScene(){
        CreateGameLogic logic = new CreateGameLogic(mock(PTScreen.class), T_Services.mockServices());
        CreateGameScene scene = (CreateGameScene) logic.getScene();
        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());
    }

    @Test
    public void testGettingGames(){

        CreateGameLogic logic = new CreateGameLogic(mock(PTScreen.class), T_Services.mockServices(new MockDB()));
        CreateGameScene scene = (CreateGameScene) logic.getScene();
        logic.getAllGames();
        logic.onGameClicked(logic.getGames().get(0));

    }


}
