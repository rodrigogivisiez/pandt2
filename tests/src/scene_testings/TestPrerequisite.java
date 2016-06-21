package scene_testings;

import abstracts.MockDB;
import abstracts.MockGamingKit;
import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.scenes.prerequisite_scene.PrerequisiteLogic;
import com.mygdx.potatoandtomato.scenes.prerequisite_scene.PrerequisiteScene;
import com.potatoandtomato.common.enums.Status;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;
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

//    private Game _game;
//
//    @Before
//    public void setUp() throws Exception {
//        _game = new Game();
//        _game.setVersion("1");
//        _game.setAbbr("test_game");
//        _game.setGameUrl("");
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void testPrerequisiteLogicScene(){
//        PrerequisiteLogic logic = new PrerequisiteLogic(mock(PTScreen.class), T_Services.mockServices(), _game, PrerequisiteLogic.JoinType.CREATING, null);
//        PrerequisiteScene scene = (PrerequisiteScene) logic.getScene();
//        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());
//    }
//
//    @Test
//    public void testHostGame(){
//        final boolean[] isCalled = new boolean[1];
//        Services _services = T_Services.mockServices();
//        _services.setGamingKit(new MockGamingKit());
//        _services.setDatabase(new MockDB(){
//            @Override
//            public void getGameByAbbr(String abbr, DatabaseListener<Game> listener) {
//                listener.onCallback(MockModel.mockGame(), Status.SUCCESS);
//            }
//
//            @Override
//            public void removeUserFromRoomOnDisconnect(String roomId, Profile user, DatabaseListener<String> listener) {
//                isCalled[0] = true;
//                listener.onCallback(null, Status.SUCCESS);
//            }
//        });
//
//
//        PTScreen screen = mock(PTScreen.class);
//        PrerequisiteLogic logic = Mockito.spy(new PrerequisiteLogic(screen, _services, _game, PrerequisiteLogic.JoinType.CREATING));
//        logic.onInit();
//        T_Threadings.sleep(100);
//        verify(logic, times(0)).joinRoomSuccess();
//        verify(logic, times(1)).createRoomSuccess(eq("123"));
//        verify(screen, times(1)).toScene(eq(SceneEnum.ROOM), any(Room.class), eq(false));
//        Assert.assertEquals(true, logic.getJoiningRoom().isOpen());
//        Assert.assertEquals(false, logic.getJoiningRoom().isPlaying());
//        Assert.assertEquals(true, isCalled[0]);
//    }
//
//
//    @Test
//    public void testJoinGame(){
//        final int[] calledCount = {0};
//        Services _services = T_Services.mockServices();
//        _services.setGamingKit(new MockGamingKit());
//
//        final Room room = MockModel.mockRoom("99");
//        room.setOpen(true);
//
//        MockDB mockDB = new MockDB(){
//            @Override
//            public void getRoomById(String id, DatabaseListener<Room> listener) {
//                listener.onCallback(room, Status.SUCCESS);
//            }
//            @Override
//            public void getGameByAbbr(String abbr, DatabaseListener<Game> listener) {
//                listener.onCallback(MockModel.mockGame(), Status.SUCCESS);
//            }
//
//            @Override
//            public void addUserToRoom(Room room, Profile user, DatabaseListener<String> listener) {
//                super.addUserToRoom(room, user, listener);
//                calledCount[0]++;
//                listener.onCallback(null, Status.SUCCESS);
//            }
//
//            @Override
//            public void removeUserFromRoomOnDisconnect(String roomId, Profile user, DatabaseListener<String> listener) {
//                calledCount[0]++;
//                listener.onCallback(null, Status.SUCCESS);
//
//            }
//        };
//        _services.setDatabase(mockDB);
//
//        PTScreen screen = mock(PTScreen.class);
//        PrerequisiteLogic logic = Mockito.spy(new PrerequisiteLogic(screen, _services, _game, PrerequisiteLogic.JoinType.JOINING, room.getId()));
//        logic.onInit();
//        T_Threadings.sleep(100);
//        verify(logic, times(1)).joinRoomSuccess();
//        verify(logic, times(0)).createRoomSuccess(anyString());
//        verify(screen, times(1)).toScene(eq(SceneEnum.ROOM), any(Room.class), eq(false));
//        Assert.assertEquals(2,  calledCount[0]);
//    }
//
//
//    @Test
//    public void testContinueGame(){
//        final int[] calledCount = {0};
//        Services _services = T_Services.mockServices();
//        _services.setGamingKit(new MockGamingKit());
//
//        _services.getProfile().setUserPlayingState(new UserPlayingState("99", false, 1));
//        final Room room = MockModel.mockRoom("99");
//        room.setRoundCounter(1);
//        room.setPlaying(true);
//        room.setOpen(false);
//
//        MockDB mockDB = new MockDB(){
//            @Override
//            public void getRoomById(String id, DatabaseListener<Room> listener) {
//                listener.onCallback(room, Status.SUCCESS);
//            }
//            @Override
//            public void getGameByAbbr(String abbr, DatabaseListener<Game> listener) {
//                listener.onCallback(MockModel.mockGame(), Status.SUCCESS);
//            }
//            @Override
//            public void addUserToRoom(Room room, Profile user, DatabaseListener<String> listener) {
//                super.addUserToRoom(room, user, listener);
//                calledCount[0]++;
//                listener.onCallback(null, Status.SUCCESS);
//
//            }
//
//            @Override
//            public void removeUserFromRoomOnDisconnect(String roomId, Profile user, DatabaseListener<String> listener) {
//                calledCount[0]++;
//                listener.onCallback(null, Status.SUCCESS);
//
//            }
//
//        };
//        _services.setDatabase(mockDB);
//
//        PTScreen screen = mock(PTScreen.class);
//        PrerequisiteLogic logic = Mockito.spy(new PrerequisiteLogic(screen, _services, _game, PrerequisiteLogic.JoinType.CONTINUING, room.getId()));
//        logic.onInit();
//        T_Threadings.sleep(100);
//        verify(logic, times(1)).joinRoomSuccess();
//        verify(logic, times(0)).createRoomSuccess(anyString());
//        verify(screen, times(1)).toScene(eq(SceneEnum.ROOM), any(Room.class), eq(true));
//        Assert.assertEquals(2,  calledCount[0]);
//    }
//
//
//    @Test
//    public void testJoinNotOpenedRoom(){
//        Services _services = T_Services.mockServices();
//        _services.setGamingKit(new MockGamingKit());
//        _services.setDatabase(new MockDB(){
//            @Override
//            public void getGameByAbbr(String abbr, DatabaseListener<Game> listener) {
//                listener.onCallback(MockModel.mockGame(), Status.SUCCESS);
//            }
//        });
//
//        final Room room = MockModel.mockRoom("99");
//        room.setOpen(false);
//
//        MockDB mockDB = new MockDB(){
//            @Override
//            public void getRoomById(String id, DatabaseListener<Room> listener) {
//                listener.onCallback(room, Status.SUCCESS);
//            }
//        };
//        _services.setDatabase(mockDB);
//
//        PTScreen screen = mock(PTScreen.class);
//        PrerequisiteLogic logic = Mockito.spy(new PrerequisiteLogic(screen, _services, _game, PrerequisiteLogic.JoinType.JOINING, room.getId()));
//        logic.onInit();
//        T_Threadings.sleep(100);
//        verify(logic, times(1)).joinRoomFailed(eq(2));
//        verify(screen, times(0)).toScene(eq(SceneEnum.ROOM), any(Room.class));
//    }
//
//    @Test
//    public void testJoinFullRoom(){
//        Services _services = T_Services.mockServices();
//        _services.setGamingKit(new MockGamingKit());
//        _services.setDatabase(new MockDB(){
//            @Override
//            public void getGameByAbbr(String abbr, DatabaseListener<Game> listener) {
//                listener.onCallback(MockModel.mockGame(), Status.SUCCESS);
//            }
//        });
//
//        final Room room = MockModel.mockRoom("99");
//        room.getGame().setMaxPlayers("1");
//        room.setOpen(true);
//
//        MockDB mockDB = new MockDB(){
//            @Override
//            public void getRoomById(String id, DatabaseListener<Room> listener) {
//                listener.onCallback(room, Status.SUCCESS);
//            }
//        };
//        _services.setDatabase(mockDB);
//
//        PTScreen screen = mock(PTScreen.class);
//        PrerequisiteLogic logic = Mockito.spy(new PrerequisiteLogic(screen, _services, _game, PrerequisiteLogic.JoinType.JOINING, room.getId()));
//        logic.onInit();
//        T_Threadings.sleep(100);
//        verify(logic, times(1)).joinRoomFailed(eq(1));
//        verify(screen, times(0)).toScene(eq(SceneEnum.ROOM), any(Room.class));
//    }
//
//    @Test
//    public void testCannotContinueGame(){
//        Services _services = T_Services.mockServices();
//        _services.setGamingKit(new MockGamingKit());
//
//        _services.getProfile().setUserPlayingState(new UserPlayingState("99", 1));
//        final Room room = MockModel.mockRoom("99");
//        room.setRoundCounter(2);
//        room.setPlaying(true);
//        room.setOpen(false);
//
//        MockDB mockDB = new MockDB(){
//            @Override
//            public void getRoomById(String id, DatabaseListener<Room> listener) {
//                listener.onCallback(room, Status.SUCCESS);
//            }
//            @Override
//            public void getGameByAbbr(String abbr, DatabaseListener<Game> listener) {
//                listener.onCallback(MockModel.mockGame(), Status.SUCCESS);
//            }
//        };
//        _services.setDatabase(mockDB);
//
//        PTScreen screen = mock(PTScreen.class);
//        PrerequisiteLogic logic = Mockito.spy(new PrerequisiteLogic(screen, _services, _game, PrerequisiteLogic.JoinType.CONTINUING, room.getId()));
//        logic.onInit();
//        T_Threadings.sleep(100);
//        verify(logic, times(0)).joinRoomSuccess();
//        verify(logic, times(0)).createRoomSuccess(anyString());
//        verify(logic, times(1)).joinRoomFailed(eq(3));
//    }
//

}
