package scene_testings;

import abstracts.MockDB;
import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.models.UserPlayingState;
import com.mygdx.potatoandtomato.scenes.game_list_scene.GameListLogic;
import com.mygdx.potatoandtomato.scenes.game_list_scene.GameListScene;
import com.potatoandtomato.common.Status;
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
        logic.onShow();
        GameListScene scene = (GameListScene) logic.getScene();
        Room room = MockModel.mockRoom("1");
        room.addInvitedUser(MockModel.mockProfile());
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

    @Test
    public void testCanContinue(){
        final Room room = MockModel.mockRoom("1");
        room.setPlaying(true);
        room.setRoundCounter(1);
        room.setOpen(false);

        Services services = T_Services.mockServices();
        services.getProfile().setUserPlayingState(new UserPlayingState(room.getId(), false, 1));
        services.setDatabase(new MockDB() {
            @Override
            public void getRoomById(String id, DatabaseListener<Room> listener) {
                listener.onCallback(room, Status.SUCCESS);
            }
        });
        GameListLogic logic = new GameListLogic(mock(PTScreen.class), services);
        Assert.assertEquals(false, ((GameListScene) logic.getScene()).getContinueGameButton().isEnabled());
        logic.onShow();
        Assert.assertEquals(true, ((GameListScene) logic.getScene()).getContinueGameButton().isEnabled());
    }



}
