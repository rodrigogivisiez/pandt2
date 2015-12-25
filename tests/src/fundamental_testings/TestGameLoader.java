package fundamental_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.Gdx;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.desktop.DesktopLauncher;
import com.mygdx.potatoandtomato.helpers.services.Downloader;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.room_scene.GameClientChecker;
import com.potatoandtomato.common.*;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.omg.PortableInterceptor.SUCCESSFUL;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by SiongLeng on 25/12/2015.
 */

public class TestGameLoader extends TestAbstract{

    Services _services;
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        _services = T_Services.mockServices();
        _services.getPreferences().deleteAll();
    }

    @Test
    public void testLoadGame(){

        final boolean[] waiting = {true};
        Game game = new Game();
        game.setGameUrl("http://www.potato-and-tomato.com/sample/core.jar");
        game.setAssetUrl("http://www.potato-and-tomato.com/sample/assets.zip");
        game.setName("Sample");
        game.setAbbr("sample");
        game.setIconUrl("http://www.potato-and-tomato.com/sample/icon.png");
        game.setMinPlayers("2");
        game.setMaxPlayers("2");
        game.setTeamCount("2");
        game.setTeamMaxPlayers("1");
        game.setTeamMinPlayers("1");
        game.setVersion("1.0");


        GameClientChecker clientChecker = new GameClientChecker(game, _services.getPreferences(), new Downloader(), new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                waiting[0] = false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(500);
        }

        waiting[0] = true;

        DesktopLauncher.subscribeLoadGameRequest();
        Broadcaster.getInstance().subscribe(BroadcastEvent.LOAD_GAME_RESPONSE, new BroadcastListener<GameLibCoordinator>() {
            @Override
            public void onCallback(GameLibCoordinator obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(false, obj.getGameEntrance().getFirstScreen() == null);
                waiting[0] = false;
            }
        });

        GameLibCoordinator gameLibCoordinator = new GameLibCoordinator(game.getFullLocalJarPath(),
                                        game.getLocalAssetsPath(), game.getFullBasePath(), new ArrayList<Team>());
        Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_GAME_REQUEST, gameLibCoordinator);

        while (waiting[0]){
            Threadings.sleep(100);
        }


    }


}
