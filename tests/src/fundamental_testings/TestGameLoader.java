package fundamental_testings;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.desktop.DesktopLauncher;
import com.mygdx.potatoandtomato.helpers.services.Downloader;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.room_scene.GameClientChecker;
import com.potatoandtomato.common.*;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        game.setGameUrl("http://www.potato-and-tomato.com/sample/game.jar");
        game.setAssetUrl("http://www.potato-and-tomato.com/sample/assets.zip");
        game.setName("Sample");
        game.setAbbr("sample");
        game.setIconUrl("http://www.potato-and-tomato.com/sample/icon.png");
        game.setMinPlayers("2");
        game.setMaxPlayers("2");
        game.setTeamCount("2");
        game.setTeamMaxPlayers("1");
        game.setTeamMinPlayers("1");
        game.setVersion("1.1");


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
        Broadcaster.getInstance().subscribe(BroadcastEvent.LOAD_GAME_RESPONSE, new BroadcastListener<GameCoordinator>() {
            @Override
            public void onCallback(GameCoordinator obj, Status st) {
                Assert.assertEquals(false, obj == null);
                Assert.assertEquals(Status.SUCCESS, st);
                waiting[0] = false;
            }
        });

        GameCoordinator gameCoordinator = new GameCoordinator(game.getFullLocalJarPath(),
                                        game.getLocalAssetsPath(), game.getBasePath(), new ArrayList<Team>(), Positions.getWidth(),
                                        Positions.getHeight(), null, null, true, "123");
        Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_GAME_REQUEST, gameCoordinator);

        while (waiting[0]){
            Threadings.sleep(100);
        }


    }


}
