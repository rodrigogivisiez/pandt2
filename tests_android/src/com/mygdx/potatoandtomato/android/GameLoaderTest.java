package com.mygdx.potatoandtomato.android;

import android.test.ActivityInstrumentationTestCase2;
import com.mygdx.potatoandtomato.absintflis.downloader.DownloaderListener;
import com.mygdx.potatoandtomato.helpers.controls.Chat;
import com.mygdx.potatoandtomato.helpers.services.*;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.room_scene.GameClientChecker;
import com.potatoandtomato.common.*;
import junit.framework.Assert;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public class GameLoaderTest extends ActivityInstrumentationTestCase2<AndroidLauncher> {

    public GameLoaderTest() {
        super(AndroidLauncher.class);
    }

    @Override
    protected void setUp() throws Exception {
        getActivity();
    }


    public void testLoadGame() {
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

        Services _services = mockServices();
        _services.getPreferences().deleteAll();

        GameClientChecker clientChecker = new GameClientChecker(game, _services.getPreferences(), new Downloader(), new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                waiting[0] = false;
            }
        });

        while (waiting[0]) {
            Threadings.sleep(500);
        }

        waiting[0] = true;

        AndroidLauncher androidLauncher = new AndroidLauncher();

        Broadcaster.getInstance().subscribe(BroadcastEvent.LOAD_GAME_RESPONSE, new BroadcastListener<GameCoordinator>() {
            @Override
            public void onCallback(GameCoordinator obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                waiting[0] = false;
            }
        });

        GameCoordinator gameCoordinator = new GameCoordinator(game.getFullLocalJarPath(),
                game.getLocalAssetsPath(), game.getBasePath(), new ArrayList<Team>(), Positions.getWidth(),
                Positions.getHeight(), null, null, false, "123");
        Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_GAME_REQUEST, gameCoordinator);

        while (waiting[0]) {
            Threadings.sleep(100);
        }
    }

    private Services mockServices() {
        Preferences preferences = new Preferences("potatoandtomato_test");
        preferences.deleteAll();
        Assets assets = new Assets();
        //assets.loadBasic(null);

        return new Services(assets, new Texts(), preferences,
                new Profile(), null, new Shaders(), null, new Downloader(), new Chat(null, null, null, null, null),
                new Socials(preferences), new GCMSender());
    }

}