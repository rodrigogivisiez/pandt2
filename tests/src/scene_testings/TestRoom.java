package scene_testings;

import abstracts.MockDB;
import abstracts.MockGamingKit;
import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.absintflis.downloader.IDownloader;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.helpers.utils.SafeThread;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.room_scene.RoomLogic;
import com.mygdx.potatoandtomato.scenes.room_scene.RoomScene;
import helpers.MockModel;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.atLeast;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by SiongLeng on 17/12/2015.
 */
public class TestRoom extends TestAbstract {

    private Services _services;
    private Room _room;

    @Before
    public void setUp() throws Exception {
        _services = T_Services.mockServices();
        _room = MockModel.mockRoom("1");
        _services.getPreferences().delete(_room.getGame().getAbbr());
        _services.setProfile(MockModel.mockProfile());
    }

    @Test
    public void testRoomLogicScene(){
        setPreferenceHasGame();
        RoomLogic logic = new RoomLogic(mock(PTScreen.class), _services, _room);
        logic.onCreate();
        RoomScene scene = (RoomScene) logic.getScene();
        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());
    }

    @Test
    public void testDownloadGame(){
        RoomLogic logic = Mockito.spy(new RoomLogic(mock(PTScreen.class), _services, _room));
        _services.setDownloader(new IDownloader() {
            @Override
            public SafeThread downloadFileToPath(String urlString, File targetFile, DownloaderListener listener) {
                int percent = 0;
                while (percent < 100){
                    percent+=50;
                    listener.onStep(percent);
                }
                listener.onCallback(null, DownloaderListener.Status.SUCCESS);
                return new SafeThread();
            }

            @Override
            public void downloadData(String url, DownloaderListener listener) {

            }
        });
        logic.onCreate();
        Threadings.sleep(4000);
        verify(logic, atLeast(2)).sendUpdateRoomMates(eq(UpdateRoomMatesCode.UPDATE_DOWNLOAD), anyString());
        verify(logic, times(1)).sendUpdateRoomMates(eq(UpdateRoomMatesCode.UPDATE_DOWNLOAD), eq(String.valueOf(100)));



    }

    @Test
    public void testStartGameCheck(){
        RoomLogic logic = Mockito.spy(new RoomLogic(mock(PTScreen.class), _services, _room));
        logic.onCreate();

        Assert.assertEquals(1, logic.startGameCheck());

        Profile user2 = MockModel.mockProfile("99");
        _room.addRoomUser(user2);
        _room.changeTeam(1, user2);
        _room.getGame().setTeamMinPlayers("1");
        _room.getGame().setTeamCount("2");

        _services.getGamingKit().onUpdateRoomMatesReceived(UpdateRoomMatesCode.UPDATE_DOWNLOAD, "50", _services.getProfile().getUserId());
        Threadings.sleep(500);
        verify(logic, times(1)).receivedUpdateRoomMates(eq(UpdateRoomMatesCode.UPDATE_DOWNLOAD), eq("50"), eq(_services.getProfile().getUserId()));
        Assert.assertEquals(2, logic.startGameCheck());
        _services.getGamingKit().onUpdateRoomMatesReceived(UpdateRoomMatesCode.UPDATE_DOWNLOAD, "100", _services.getProfile().getUserId());
        Threadings.sleep(500);
        verify(logic, times(1)).receivedUpdateRoomMates(eq(UpdateRoomMatesCode.UPDATE_DOWNLOAD), eq("100"), eq(_services.getProfile().getUserId()));
        Assert.assertEquals(0, logic.startGameCheck());
    }

    @Test
    public void testHostLeft(){
        RoomLogic logic = Mockito.spy(new RoomLogic(mock(PTScreen.class), _services, _room));

        _services.setDatabase(new MockDB(){
            @Override
            public void monitorRoomById(String id, DatabaseListener<Room> listener) {
                super.monitorRoomById(id, listener);
                _room.getRoomUsers().remove(_room.getHost().getUserId());
                listener.onCallback(_room, DatabaseListener.Status.SUCCESS);
            }
        });

        logic.onCreate();

        verify(logic, times(1)).checkHostInRoom();
        Assert.assertEquals(false, logic.checkHostInRoom());
    }

    @Test
    public void testJoinRoom(){
        _room.getRoomUsers().clear();

        _room.addRoomUser(MockModel.mockProfile("1"));      //0
        _room.addRoomUser(MockModel.mockProfile("2"));      //1
        _room.addRoomUser(MockModel.mockProfile("3"), 3);       //3
        _room.addRoomUser(MockModel.mockProfile("4"));      //2

        Assert.assertEquals(4, _room.getRoomUsersCount());
        Assert.assertEquals(0, _room.getSlotIndexByUserId(MockModel.mockProfile("1")));
        Assert.assertEquals(1, _room.getSlotIndexByUserId(MockModel.mockProfile("2")));
        Assert.assertEquals(2, _room.getSlotIndexByUserId(MockModel.mockProfile("4")));
        Assert.assertEquals(3, _room.getSlotIndexByUserId(MockModel.mockProfile("3")));

    }



    @Test
    public void testGameStartOrEndParameters(){
        RoomLogic logic = Mockito.spy(new RoomLogic(mock(PTScreen.class), _services, _room));
        logic.onCreate();

        Assert.assertEquals(false, _room.isPlaying());
        Assert.assertEquals(true, _room.isOpen());
        Assert.assertEquals(0, _room.getRoundCounter());

        Profile user2 = MockModel.mockProfile("another");
        _room.changeTeam(1, user2);
        _room.getGame().setTeamMinPlayers("1");
        _room.getGame().setTeamCount("2");

        logic.startGame();
        Threadings.sleep(3100);

        Assert.assertEquals(true, _room.isPlaying());
        Assert.assertEquals(false, _room.isOpen());
        Assert.assertEquals(1, _room.getRoundCounter());
        verify(logic, times(1)).gameStarted();

        logic.gameFinished();
        Assert.assertEquals(false, _room.isPlaying());
        Assert.assertEquals(true, _room.isOpen());
        Assert.assertEquals(1, _room.getRoundCounter());

    }

    @Test
    public void testHostLeaveRoomParameters(){

        GamingKit mockKit = Mockito.spy(new MockGamingKit());
        _services.setGamingKit(mockKit);
        RoomLogic logic = Mockito.spy(new RoomLogic(mock(PTScreen.class), _services, _room));
        logic.onCreate();

        logic.leaveRoom();

        verify(mockKit, times(1)).leaveRoom();
        Assert.assertEquals(false, _room.isOpen());
        Assert.assertEquals(-1, _room.getSlotIndexByUserId(_services.getProfile()));

    }


    @Test
    public void testUserLeaveRoomParameters(){

        GamingKit mockKit = Mockito.spy(new MockGamingKit());
        _room.setOpen(true);
        _services.setGamingKit(mockKit);
        _services.setProfile(MockModel.mockProfile("another"));
        RoomLogic logic = Mockito.spy(new RoomLogic(mock(PTScreen.class), _services, _room));
        logic.onCreate();

        logic.leaveRoom();

        verify(mockKit, times(1)).leaveRoom();
        Assert.assertEquals(true, _room.isOpen());
        Assert.assertEquals(-1, _room.getSlotIndexByUserId(_services.getProfile()));

    }


    private void setPreferenceHasGame(){
        _services.getPreferences().put(_room.getGame().getAbbr(), _room.getGame().getVersion());

    }


}
