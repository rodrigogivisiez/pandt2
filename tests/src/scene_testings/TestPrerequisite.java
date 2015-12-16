package scene_testings;

import abstracts.MockDownloader;
import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.absintflis.downloader.IDownloader;
import com.mygdx.potatoandtomato.enums.MascotEnum;
import com.mygdx.potatoandtomato.helpers.utils.Zippings;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.prerequisite_scene.PrerequisiteLogic;
import com.mygdx.potatoandtomato.scenes.prerequisite_scene.PrerequisiteScene;
import helpers.T_Services;
import helpers.T_Threadings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class TestPrerequisite extends TestAbstract {

    private Services _services;
    private Game _game;

    @Before
    public void setUp() throws Exception {
        _services = T_Services.mockServices();
        _services.getPreferences().deleteAll();
        _game = new Game();
        _game.setVersion("1");
        _game.setAbbr("test_game");
        _game.setAssetUrl("");
        _game.setGameUrl("");
    }

    @After
    public void tearDown() throws Exception {
        _services = T_Services.mockServices();
        _services.getPreferences().deleteAll();
    }

    @Test
    public void testCreateGameLogicScene(){
        PrerequisiteLogic logic = new PrerequisiteLogic(mock(PTScreen.class), T_Services.mockServices(), _game, true);
        PrerequisiteScene scene = (PrerequisiteScene) logic.getScene();
        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());
    }

    @Test
    public void testGameClientAlreadyExist(){

        _services.getPreferences().put(_game.getAbbr(), _game.getVersion());

        PrerequisiteLogic logic = Mockito.spy(new PrerequisiteLogic(mock(PTScreen.class), _services, _game, true));
        logic.init();
        T_Threadings.sleep(100);
        verify(logic, times(0)).downloadFile(anyString(), any(File.class), anyDouble(), any(Runnable.class));
        verify(logic, times(1)).createRoom();
        verify(logic, times(1)).joinRoomSuccess(anyString());
        Assert.assertEquals(_game.getVersion(), _services.getPreferences().get(_game.getAbbr()));
    }

    @Test
    public void testGameClientNotfound(){
        PrerequisiteLogic logic = Mockito.spy(new PrerequisiteLogic(mock(PTScreen.class), _services, _game, true));
        logic.init();
        T_Threadings.sleep(100);
        verify(logic, times(2)).downloadFile(anyString(), any(File.class), anyDouble(), any(Runnable.class));
        verify(logic, times(1)).createRoom();
        verify(logic, times(1)).joinRoomSuccess(anyString());
        Assert.assertEquals(_game.getVersion(), _services.getPreferences().get(_game.getAbbr()));
    }

    @Test
    public void testGameClientDownloadFailed(){
        IDownloader downloader = mock(MockDownloader.class);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                DownloaderListener downloaderListener = (DownloaderListener) arguments[2];
                downloaderListener.onCallback(null, DownloaderListener.Status.FAILED);
                return null;
            }
        }).when(downloader).downloadFileToPath(anyString(), any(File.class), any(DownloaderListener.class));

        _services = T_Services.mockServices(downloader);
        PrerequisiteLogic logic = Mockito.spy(new PrerequisiteLogic(mock(PTScreen.class), _services, _game, true));
        logic.init();
        T_Threadings.sleep(100);
        verify(logic, times(2)).downloadFile(anyString(), any(File.class), anyDouble(), any(Runnable.class));
        verify(logic, times(2)).getGameClientFailed();
        verify(logic, times(0)).joinRoomSuccess(anyString());
        Assert.assertEquals(null, _services.getPreferences().get(_game.getAbbr()));
    }



}
