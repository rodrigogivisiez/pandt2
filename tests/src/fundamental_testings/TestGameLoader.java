package fundamental_testings;

import abstracts.MockDB;
import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.game_file_checker.GameFileCheckerListener;
import com.mygdx.potatoandtomato.desktop.DesktopLauncher;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.absints.*;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.Downloader;
import com.mygdx.potatoandtomato.services.VersionControl;
import com.mygdx.potatoandtomato.utils.Positions;
import com.potatoandtomato.common.utils.Threadings;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.room_scene.GameFileChecker;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import helpers.T_Services;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 25/12/2015.
 */

public class TestGameLoader extends TestAbstract{

    Services _services;
    //@Override
    @Before
    public void setUp() throws Exception {
        //super.setUp();
        _services = T_Services.mockServices();
        _services.getPreferences().deleteAll();
    }

    @Test
    public void testLoadGame(){

        final boolean[] waiting = {true};
        final Game game = new Game();
        game.setGameUrl("http://cdn.shephertz.com/repository/files/c7236c0f55a51bcdde0415e639f2e87f73178a02cdd5d41485e19ad15334c56f/f81fc395d173a69ad5df3daa31c07aa623316136/sample_game.zip");
        game.setName("Sample");
        game.setAbbr("sample");
        game.setIconUrl("http://cdn.shephertz.com/repository/files/c7236c0f55a51bcdde0415e639f2e87f73178a02cdd5d41485e19ad15334c56f/5aa71b4ed51b4637128a88583bea5f3df491219d/sample_icon.png");
        game.setMinPlayers("2");
        game.setMaxPlayers("2");
        game.setTeamCount("2");
        game.setTeamMaxPlayers("1");
        game.setTeamMinPlayers("1");
        game.setVersion("1.1");
        game.setCommonVersion("1");


        GameFileChecker clientChecker = new GameFileChecker(game, _services.getPreferences(),
                new Downloader(), new MockDB(){
            @Override
            public void getGameByAbbr(String abbr, DatabaseListener<Game> listener) {
                listener.onCallback(game, Status.SUCCESS);
            }
        }, new VersionControl(), new GameFileCheckerListener() {
            @Override
            public void onCallback(GameFileChecker.GameFileResult result, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                waiting[0] = false;
            }

        });

        while (waiting[0]){
            Threadings.sleep(500);
        }

        waiting[0] = true;

        Broadcaster broadcaster = new Broadcaster();

        DesktopLauncher._broadcaster = broadcaster;
        DesktopLauncher.subscribeLoadGameRequest();
        broadcaster.subscribe(BroadcastEvent.LOAD_GAME_RESPONSE, new BroadcastListener<GameCoordinator>() {
            @Override
            public void onCallback(GameCoordinator obj, Status st) {
                //Assert.assertEquals(false, obj == null);
                //Assert.assertEquals(Status.SUCCESS, st);
                waiting[0] = false;
            }
        });

        GameCoordinator gameCoordinator = new GameCoordinator(game.getFullLocalJarPath(),
                                        game.getLocalAssetsPath(), game.getBasePath(), new ArrayList<Team>(), Positions.getWidth(),
                                        Positions.getHeight(), null, null, "123", mock(IGameSandBox.class), null, "1", mock(ISoundsPlayer.class), broadcaster,
                                        mock(IDownloader.class), mock(ITutorials.class), mock(GamePreferencesAbstract.class), Global.LEADERBOARD_COUNT);
        broadcaster.broadcast(BroadcastEvent.LOAD_GAME_REQUEST, gameCoordinator);

        while (waiting[0]){
            Threadings.sleep(100);
        }


    }


}
