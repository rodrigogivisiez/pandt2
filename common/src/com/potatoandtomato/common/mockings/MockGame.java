package com.potatoandtomato.common.mockings;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.firebase.client.Firebase;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.absints.*;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.controls.DisposableActor;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.statics.CommonVersion;
import com.potatoandtomato.common.utils.ColorUtils;
import com.potatoandtomato.common.utils.Downloader;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;

import javax.xml.bind.annotation.XmlElementDecl;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 27/12/2015.
 */
public abstract class MockGame extends Game implements IPTGame {

    Array<InputProcessor> _processors;
    SpriteBatch _spriteBatch;
    MockGamingKit _mockGamingKit;
    GameCoordinator _gameCoordinator;
    Broadcaster _broadcaster;
    Downloader _downloader;
    PTAssetsManager _monitoringPTAssetsManager;

    public MockGame(String gameId) {

        _broadcaster = new Broadcaster();
        _downloader = new Downloader();

        try {
            PrintWriter out = new PrintWriter("common_version.txt");
            out.print(CommonVersion.VERSION);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Firebase _ref = new Firebase("https://pttestgame.firebaseio.com");


        _processors = new Array<InputProcessor>();
        _gameCoordinator = new GameCoordinator("", "", "", new ArrayList<Team>(), 360, 640, this, _spriteBatch, "", new IGameSandBox() {
            @Override
            public void useConfirm(String msg, Runnable yesRunnable, Runnable noRunnable) {
                System.out.println("show confirm: " + msg);
                if (msg.equals("PTTEXT_ABANDON")) {
                    yesRunnable.run();
                    _mockGamingKit.sendUpdate("SURRENDER");
                }
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
                _mockGamingKit.sendUpdate(msg);
            }

            @Override
            public void updateScores(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers) {

            }
        }, _ref.child("gameBelongData").child(gameId), "1", new MockSoundManager(),
                _broadcaster, _downloader, new ITutorials() {
            @Override
            public void show(DisposableActor actor, String text, float duration) {
                System.out.println("Showing tutorial: " + text);
            }
        }, new GamePreferencesAbstract() {
            @Override
            public String getGamePref(String key) {
                return "";
            }

            @Override
            public void putGamePref(String key, String value) {

            }

            @Override
            public void deleteGamePref(String key) {

            }
        }, 20);
    }

    public void initiateMockGamingKit(final int expectedTeamCount, final int eachTeamExpectedPlayers, int delay, final boolean debugging){
        _mockGamingKit = new MockGamingKit(_gameCoordinator, expectedTeamCount, !debugging ? eachTeamExpectedPlayers : 0, delay, _broadcaster, new Runnable() {
            @Override
            public void run() {
                _gameCoordinator.setMyUserId(_mockGamingKit.getUserId());

                if(debugging){
                    boolean addedMe = false;
                    ArrayList<Team> teams = new ArrayList<Team>();
                    int index = 0;
                    for(int i = 0; i < expectedTeamCount; i++){
                        Team team = new Team();
                        for(int q = 0; q < eachTeamExpectedPlayers; q++){
                            team.addPlayer(new Player("test", !addedMe ? _mockGamingKit.getUserId() : Strings.generateUniqueRandomKey(18), true, true,
                                                    index));
                            addedMe = true;
                            index++;
                        }
                        teams.add(team);
                    }

                    _gameCoordinator.setTeams(teams);
                }


                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        while (_monitoringPTAssetsManager != null && !_monitoringPTAssetsManager.isFinishLoading()){
                            Threadings.sleep(100);
                        }

                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onReady();
                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    public void create() {
        _spriteBatch = new SpriteBatch();
        _gameCoordinator.setSpriteBatch(_spriteBatch);
    }

    public SpriteBatch getSpriteBatch() {
        return _spriteBatch;
    }

    public GameCoordinator getCoordinator() {
        return _gameCoordinator;
    }

    @Override
    public void addInputProcessor(InputProcessor processor, int index){
        _processors.insert(index, processor);
        setInputProcessors();
    }

    @Override
    public void addInputProcessor(InputProcessor processor) {
        _processors.add(processor);
        setInputProcessors();
    }

    @Override
    public void removeInputProcessor(InputProcessor processor) {
        _processors.removeValue(processor, false);
        setInputProcessors();
    }

    private void setInputProcessors(){
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.setProcessors(_processors);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public abstract void onReady();

    @Override
    public void render() {
        super.render();
        if(_monitoringPTAssetsManager != null && !_monitoringPTAssetsManager.isFinishLoading() && _monitoringPTAssetsManager.update()) {
            _monitoringPTAssetsManager.setFinishLoading(true);
        }
    }

    @Override
    public void monitorPTAssetManager(PTAssetsManager ptAssetsManager) {
        _monitoringPTAssetsManager = ptAssetsManager;
    }
}
