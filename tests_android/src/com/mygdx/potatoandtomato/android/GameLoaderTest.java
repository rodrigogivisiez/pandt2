package com.mygdx.potatoandtomato.android;

import android.test.ActivityInstrumentationTestCase2;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.game_file_checker.GameFileCheckerListener;
import com.mygdx.potatoandtomato.helpers.services.Chat;
import com.mygdx.potatoandtomato.helpers.services.*;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.Threadings;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.scenes.room_scene.GameFileChecker;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.absints.IGameSandBox;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.Downloader;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public class GameLoaderTest extends ActivityInstrumentationTestCase2<AndroidLauncher> {

    private Broadcaster broadcaster;

    public GameLoaderTest() {
        super(AndroidLauncher.class);
    }

    @Override
    protected void setUp() throws Exception {
        AndroidLauncher launcher = getActivity();
        broadcaster = launcher.getBroadcaster();
    }


    public void testLoadGame() {
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

        Services _services = mockServices();
        _services.getPreferences().deleteAll();

        GameFileChecker clientChecker = new GameFileChecker(game, _services.getPreferences(), new Downloader(), new MockDB(){
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

        while (waiting[0]) {
            Threadings.sleep(500);
        }

        waiting[0] = true;

        broadcaster.subscribe(BroadcastEvent.LOAD_GAME_RESPONSE, new BroadcastListener<GameCoordinator>() {
            @Override
            public void onCallback(GameCoordinator obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                waiting[0] = false;
            }
        });

        GameCoordinator gameCoordinator = new GameCoordinator(game.getFullLocalJarPath(),
                game.getLocalAssetsPath(), game.getBasePath(), new ArrayList<Team>(), Positions.getWidth(),
                Positions.getHeight(), null, null, "123", new IGameSandBox() {
            @Override
            public void useConfirm(String msg, Runnable yesRunnable, Runnable noRunnable) {

            }

            @Override
            public void userAbandoned(String userId) {

            }

            @Override
            public void onGameLoaded() {

            }

            @Override
            public void endGame() {

            }

            @Override
            public void inGameUpdateRequest(String msg) {

            }

            @Override
            public void updateScores(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers) {

            }
        }, null, "1", null, broadcaster, new Downloader(), null, null);
        broadcaster.broadcast(BroadcastEvent.LOAD_GAME_REQUEST, gameCoordinator);

        while (waiting[0]) {
            Threadings.sleep(100);
        }
    }

    private Services mockServices() {
        Preferences preferences = new Preferences("potatoandtomato_test");
        preferences.deleteAll();
        Assets assets = new Assets(null, null, null, null, null, null);
        //assets.loadBasic(null);

        return new Services(assets, new Texts(), preferences,
                new Profile(), null, new Shaders(), null, new Downloader(), new Chat(null, null, null, null, null, null, null, null, broadcaster),
                new Socials(preferences, broadcaster), new GCMSender(), null, null, null, null, null, null, broadcaster, null);
    }

}