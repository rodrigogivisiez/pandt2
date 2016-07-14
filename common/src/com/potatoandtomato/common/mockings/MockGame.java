package com.potatoandtomato.common.mockings;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.firebase.client.Firebase;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.absints.*;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.controls.DisposableActor;
import com.potatoandtomato.common.enums.ConfirmMsgType;
import com.potatoandtomato.common.enums.RoomUpdateType;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.helpers.DesktopImageLoader;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.SpeechAction;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.statics.CommonVersion;
import com.potatoandtomato.common.utils.*;

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
    PTAssetsManager _monitoringPTAssetsManager;
    ArrayList<Runnable> _onResumeRunnables;
    DesktopImageLoader _desktopImageLoader;
    boolean isContinue;
    boolean isDebugging;
    ArrayList<Runnable> _msgToSendRunnables;
    boolean isMockKitReady;


    public MockGame(String gameId, final boolean isContinue) {
        _onResumeRunnables = new ArrayList();
        _msgToSendRunnables = new ArrayList();
        _broadcaster = new Broadcaster();
        _desktopImageLoader = new DesktopImageLoader(_broadcaster);
        this.isContinue = isContinue;

        try {
            PrintWriter out = new PrintWriter("common_version.txt");
            out.print(CommonVersion.VERSION);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Firebase _ref = new Firebase("https://pttestgame.firebaseio.com");

        _processors = new Array<InputProcessor>();
        _gameCoordinator = new GameCoordinator("", "", new ArrayList<Team>(), 360, 640, this, _spriteBatch, "", new IGameSandBox() {
            @Override
            public void useConfirm(ConfirmMsgType msgType, Runnable yesRunnable, Runnable noRunnable) {
                if (msgType == ConfirmMsgType.AbandonGameNoCons ||
                        msgType == ConfirmMsgType.AbandonGameConsLoseStreak) {
                    yesRunnable.run();
                    _mockGamingKit.sendUpdate(-1, "SURRENDER", false, "");
                }
            }

            @Override
            public void useConfirm(String msg, Runnable yesRunnable, Runnable noRunnable) {

            }

            @Override
            public void useNotification(String msg) {

            }

            @Override
            public void userAbandoned(String userId) {

            }


            @Override
            public void endGame() {

            }

            @Override
            public void sendUpdate(final RoomUpdateType updateType, final String msg) {
                if (!isMockKitReady) {
                    _msgToSendRunnables.add(new Runnable() {
                        @Override
                        public void run() {
                            sendUpdate(updateType, msg);
                        }
                    });
                    return;
                }

                int code = -1;
                if (updateType == RoomUpdateType.GameData) {
                    code = 0;
                } else if (updateType == RoomUpdateType.DecisionMakerUpdate) {
                    code = 1;
                }

                _mockGamingKit.sendUpdate(code, msg, false, "");
            }

            @Override
            public void sendPrivateUpdate(final RoomUpdateType updateType, final String toUserId, final String msg) {
                if (!isMockKitReady) {
                    _msgToSendRunnables.add(new Runnable() {
                        @Override
                        public void run() {
                            sendPrivateUpdate(updateType, toUserId, msg);
                        }
                    });
                    return;
                }


                int code = -1;
                if (updateType == RoomUpdateType.GameData) {
                    code = 0;
                } else if (updateType == RoomUpdateType.DecisionMakerUpdate) {
                    code = 1;
                }

                _mockGamingKit.sendUpdate(code, msg, true, toUserId);
            }

            @Override
            public void vibrate(double periodInMili) {

            }

            @Override
            public void finalizing(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers, boolean abandoned) {

            }

            @Override
            public void gameFailed(String msg) {
                System.out.println("Game failed to resume " + msg);
            }

        }, _ref.child("gameBelongData").child(gameId), "1", 0, new MockSoundManager(),
                new IRemoteHelper() {
                    @Override
                    public void getRemoteImage(final String url, final WebImageListener listener) {
                        _broadcaster.subscribe(BroadcastEvent.LOAD_IMAGE_RESPONSE, new BroadcastListener<Pair<String, Texture>>() {
                            @Override
                            public void onCallback(Pair<String, Texture> result, Status st) {
                                if (st == Status.SUCCESS) {
                                    if (result.getFirst().equals(url)) {
                                        _broadcaster.unsubscribe(this.getId());
                                        listener.onLoaded(result.getSecond());
                                    }
                                } else {
                                    if (result != null) {
                                        if (result.getFirst() != null && result.getFirst().equals(url)) {
                                            _broadcaster.unsubscribe(this.getId());
                                            listener.onLoaded(null);
                                        }
                                    }
                                }
                            }
                        });
                        _broadcaster.broadcast(BroadcastEvent.LOAD_IMAGE_REQUEST, url);
                    }

                    @Override
                    public void dispose() {

                    }
                }, new ITutorials() {
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
        }, 20, new IDisconnectOverlayControl() {
            @Override
            public void showResumingGameOverlay(int remainingMiliSecs) {

            }

            @Override
            public void hideOverlay() {

            }
        }, new ICoins() {
            @Override
            public void showCoinMachine() {

            }

            @Override
            public void hideCoinMachine() {

            }

            @Override
            public void reset() {

            }

            @Override
            public void requestCoinsMachineStateFromOthers() {

            }

            @Override
            public void initCoinMachine(String coinsPurpose, int expectingCoin, String transactionId, ArrayList<Pair<String, String>> userIdToNamePairs, boolean requestFromOthers, ArrayList<SpeechAction> potatoSpeechActions, ArrayList<SpeechAction> tomatoSpeechActions, String dismissText) {

            }


            @Override
            public void startDeductCoins() {

            }

            @Override
            public void setCoinListener(CoinListener coinListener) {

            }
        }){
            @Override
            public void finishLoading() {
                super.finishLoading();
                _gameCoordinator.setGameStarted(true, isContinue);
                _mockGamingKit.setGameStarted(true);
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Assets loaded.");
                    }
                });
            }
        };


    }

    public void initiateMockGamingKit(final int expectedTeamCount, final int eachTeamExpectedPlayers, int delay, final boolean debugging){
        isDebugging = debugging;
        _mockGamingKit = new MockGamingKit(_gameCoordinator, expectedTeamCount, !debugging ? eachTeamExpectedPlayers : 0, delay);
        _gameCoordinator.setMyUserId(_mockGamingKit.getUserId());

        if(debugging){
            boolean addedMe = false;
            ArrayList<Team> teams = new ArrayList<Team>();
            int index = 0;
            for(int i = 0; i < expectedTeamCount; i++){
                Team team = new Team();
                for(int q = 0; q < eachTeamExpectedPlayers; q++){
                    team.addPlayer(new Player("test", !addedMe ? _mockGamingKit.getUserId() : Strings.generateUniqueRandomKey(18), true, index));
                    addedMe = true;
                    index++;
                }
                teams.add(team);
            }

            _gameCoordinator.setTeams(teams);
            _gameCoordinator.getDecisionsMaker().teamsInit(teams);
        }

        connectMockGamingKit();

    }

    private void connectMockGamingKit(){
        _mockGamingKit.connect(new Runnable() {
            @Override
            public void run() {
                isMockKitReady = true;
                for(Runnable runnable : _msgToSendRunnables){
                    runnable.run();
                }

                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Connect Done.");
                        onReady();
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
    public void addInputProcessor(InputProcessor processor, int index, boolean external){
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

        if (Gdx.input.isKeyPressed(Input.Keys.F10)){
            _mockGamingKit.setPausing(true);
            Gdx.input.setInputProcessor(null);
            System.out.println("Game Disconnected But Recoverable");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.F11)){
            _mockGamingKit.setPausing(false);
            setInputProcessors();
            System.out.println("Game Recovered");
        }

    }

    @Override
    public void monitorPTAssetManager(PTAssetsManager ptAssetsManager) {
        _monitoringPTAssetsManager = ptAssetsManager;
    }


    @Override
    public void resume() {
        super.resume();
        for(Runnable runnable : _onResumeRunnables){
            runnable.run();
        }
    }

    @Override
    public void dispose() {
        if(_mockGamingKit != null){
            _mockGamingKit.disconnect();
        }
    }

    @Override
    public void addOnResumeRunnable(Runnable toRun) {
        _onResumeRunnables.add(toRun);
    }

    @Override
    public void removeOnResumeRunnable(Runnable toRun) {
        _onResumeRunnables.remove(toRun);
    }

    public boolean isContinue() {
        return isContinue;
    }
}
