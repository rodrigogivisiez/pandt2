package scene_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.scenes.game_list_scene.GameListLogic;
import com.mygdx.potatoandtomato.scenes.game_list_scene.GameListScene;
import helpers.MockModel;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class TestGameList extends TestAbstract {

    @Test
    public void testGameListLogicScene(){
        GameListLogic logic = new GameListLogic(mock(PTScreen.class), T_Services.mockServices());
        GameListScene scene = (GameListScene) logic.getScene();
        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());
    }

    @Test
    public void testGameListLogicSceneAddRemoveRecord(){
        GameListLogic logic = new GameListLogic(mock(PTScreen.class), T_Services.mockServices());
        logic.onCreate();
        GameListScene scene = (GameListScene) logic.getScene();
        Room room = MockModel.mockRoom("1");
        room.setOpen(true);
        for(int i = 0; i<20; i++){
            logic.roomDataChanged(room);
        }

        Threadings.sleep(100);

        scene.gameRowHighlight("0");
        Assert.assertEquals(1, scene.getGameRowsCount());

        room.setOpen(false);
        logic.roomDataChanged(room);

        Threadings.sleep(100);
        Assert.assertEquals(0, scene.getGameRowsCount());

    }


}
