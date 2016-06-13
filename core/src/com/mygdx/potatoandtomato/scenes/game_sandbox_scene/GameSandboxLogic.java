package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.absintflis.scenes.ConnectionsControllerListener;
import com.mygdx.potatoandtomato.absintflis.scenes.GameLoadStateMonitorListener;
import com.mygdx.potatoandtomato.absintflis.services.ConnectionWatcherListener;
import com.mygdx.potatoandtomato.enums.ConnectionStatus;
import com.mygdx.potatoandtomato.enums.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.LeaderboardType;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.services.Confirm;
import com.mygdx.potatoandtomato.services.Notification;
import com.mygdx.potatoandtomato.scenes.leaderboard_scene.EndGameLeaderBoardLogic;
import com.potatoandtomato.common.enums.RoomUpdateType;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.utils.*;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.absints.IGameSandBox;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public class GameSandboxLogic extends LogicAbstract implements IGameSandBox {

    GameSandboxScene _scene;
    Room room;
    GameCoordinator coordinator;
    Notification _notification;
    boolean isContinue;
    boolean gameStarted;
    boolean gamePlaying;
    boolean failed;
    boolean exiting;
    EndGameData endGameData;
    EndGameLeaderBoardLogic leaderboardLogic;
    HashMap<Team, ArrayList<ScoreDetails>> winners;
    ArrayList<Team> losers;
    SafeThread safeThread;
    GameLoadStateMonitor gameLoadStateMonitor;
    ConnectionsController connectionsController;
    CopyOnWriteArrayList<Runnable> onGameStartedRunnables;
    CopyOnWriteArrayList<Runnable> onHasGameDataReceivedRunnables;

    public GameSandboxLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        setSaveToStack(false);
        failed = false;
        _notification = services.getNotification();
        _scene = new GameSandboxScene(_services, _screen);
        room = (Room) objs[0];
        isContinue = (Boolean) objs[1];
        safeThread = new SafeThread();
        gameLoadStateMonitor = new GameLoadStateMonitor(room, _services, isContinue ? null : room.getTeams(), _screen, this);
        connectionsController = new ConnectionsController(room, services);
        onGameStartedRunnables = new CopyOnWriteArrayList<Runnable>();
        onHasGameDataReceivedRunnables = new CopyOnWriteArrayList<Runnable>();

        initiateUserTables();
        initiateEndGameEssential();
        _services.getChat().hideChat();
        _services.getConfirm().close();

        setListenersAndThreads();
        Threadings.setContinuousRenderLock(true);
    }

    @Override
    public void onQuit(final OnQuitListener listener) {
        listener.onResult(gameStarted || failed ? OnQuitListener.Result.YES : OnQuitListener.Result.NO);
    }

    public void updateReceived(final int code, final String msg, final String senderId){
        if(!gamePlaying){
            if(code == UpdateRoomMatesCode.IN_GAME_UPDATE){
                onHasGameDataReceivedRunnables.add(new Runnable() {
                    @Override
                    public void run() {
                        updateReceived(code, msg, senderId);
                    }
                });
                return;
            }
        }

        if(!gameStarted){
            if(code != UpdateRoomMatesCode.IN_GAME_UPDATE){
                onGameStartedRunnables.add(new Runnable() {
                    @Override
                    public void run() {
                        updateReceived(code, msg, senderId);
                    }
                });
                return;
            }
        }

        if(code == UpdateRoomMatesCode.IN_GAME_UPDATE){
            coordinator.receivedRoomUpdate(msg, senderId);
        }
        else if(code == UpdateRoomMatesCode.GAME_DATA){
            coordinator.getGameDataHelper().receivedGameData(msg);
        }
        else if(code == UpdateRoomMatesCode.GAME_DATA_REQUEST){
            coordinator.getGameDataHelper().receivedGameDataRequest(senderId);
        }
        else if(code == UpdateRoomMatesCode.DECISION_MAKER){
            coordinator.getDecisionsMaker().receivedDecisionMakerUpdate(msg);
        }
        else if(code == UpdateRoomMatesCode.KICK_USER){         //user still in game but getting kicked by host, straightly quit game
            JsonObj jsonObj = new JsonObj(msg);
            String kickedUserId = jsonObj.getString("userId");
            if(kickedUserId.equals(_services.getProfile().getUserId())){
                _notification.important(_texts.notificationYouKicked());
                connectionsController.sendUserAbandoned(kickedUserId);
                _services.getGamingKit().leaveRoom();
                connectionsController.receivedUserAbandoned(kickedUserId, "");
                exitSandbox();
            }
            else{
                Player player = room.getPlayerByUserId(kickedUserId);
                _notification.important(String.format(_texts.notificationKicked(), player != null ? player.getName() : ""));
            }
        }
    }

    private void initiateUserTables(){
        if(isContinue){
            Profile myProfile = _services.getProfile();
            setUserTableDesign(myProfile.getUserId(), false, false);
        }
        else{
            for(Team team : room.getTeams()){
                for(Player player : team.getPlayers()){
                    setUserTableDesign(player.getUserId(), false, false);
                }
            }
        }
    }

    private void initiateEndGameEssential(){
        endGameData = new EndGameData(room, _services.getProfile().getUserId());
        if(room.getGame().hasLeaderboard()){
            leaderboardLogic = new EndGameLeaderBoardLogic(_screen, _services, endGameData,
                                                            room.getUserTeam(_services.getProfile().getUserId()).getPlayers());
        }
    }

    public void gameStart(){
        if(gameStarted) return;

        final Runnable startGameRunnable = new Runnable() {
            @Override
            public void run() {
                connectionsController.gameStarted();

                _screen.switchToGameScreen();
                if(!isContinue){
                    coordinator.getGameEntrance().init();
                }
                else{
                    coordinator.getGameEntrance().onContinue();
                    connectionsController.sendMeConnected();
                }

                gameStarted = true;

                //game data helper kicks in from here
                coordinator.setGameStarted(true, isContinue);

                _scene.clearRoot();
                _services.getChat().setMode(2);
                _services.getChat().resetChat();
                _services.getChat().showChat();
                if(coordinator.isLandscape()){
                    _services.getBroadcaster().broadcast(BroadcastEvent.DEVICE_ORIENTATION, 1);
                }

                _services.getSoundsPlayer().stopThemeMusic();
                //for multitask still play theme music bug fix
                Threadings.delay(3000, new Runnable() {
                    @Override
                    public void run() {
                        _services.getSoundsPlayer().stopThemeMusic();
                    }
                });

                for(Runnable runnable : onGameStartedRunnables){
                    runnable.run();
                }
                onGameStartedRunnables.clear();

                coordinator.getGameDataHelper().setOnGameDataReceivedRunnable(new OneTimeRunnable(new Runnable() {
                    @Override
                    public void run() {
                        for(Runnable runnable : onHasGameDataReceivedRunnables){
                            runnable.run();
                        }
                        onHasGameDataReceivedRunnables.clear();

                        gamePlaying = true;
                    }
                }));

            }
        };

        if(isContinue){
            connectionsController.refreshAllConnectStates(new Runnable() {
                @Override
                public void run() {
                    Threadings.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            startGameRunnable.run();
                        }
                    });
                }
            });
        }
        else{
            Threadings.delay(500, new Runnable() {
                @Override
                public void run() {
                    startGameRunnable.run();
                }
            });
        }


    }

    public void failLoad(Player player){
        failed = true;

        _services.getChat().newMessage(new ChatMessage(_texts.loadGameFailed(),
                                ChatMessage.FromType.IMPORTANT, null, ""));

        Threadings.delay(2000, new Runnable() {
            @Override
            public void run() {
                exitSandbox();
            }
        });
    }

    public void setUserTableDesign(final String userId, final boolean isReady, final boolean isFailed){
        Player player = room.getPlayerByUserId(userId);
        _scene.setUser(userId, player.getName(), isReady, isFailed, player.getUserColor());
    }


    public void setListenersAndThreads(){
        gameLoadStateMonitor.setGameLoadStateMonitorListener(new GameLoadStateMonitorListener() {
            @Override
            public void onAllSuccess(GameCoordinator gameCoordinator) {
                coordinator = gameCoordinator;
                gameStart();
            }

            @Override
            public void onPlayerReady(Player player) {
                setUserTableDesign(player.getUserId(), true, false);
            }

            @Override
            public void onFailed(Player failedPlayers) {
                setUserTableDesign(failedPlayers.getUserId(), false, true);
                failLoad(failedPlayers);
            }

        });

        connectionsController.setConnectionsControllerListener(new ConnectionsControllerListener() {
            @Override
            public void userConnectionChanged(final String userId, final ConnectionStatus connectionStatus) {
                if(!gameStarted){
                    onGameStartedRunnables.add(new Runnable() {
                        @Override
                        public void run() {
                            userConnectionChanged(userId, connectionStatus);
                        }
                    });
                    return;
                }


                if(connectionStatus == ConnectionStatus.Abandoned){
                    coordinator.userAbandon(userId);
                    if(userId.equals(_services.getProfile().getUserId())){
                        endGame();
                    }
                }
                else if(connectionStatus == ConnectionStatus.Disconnected){
                    coordinator.userConnectionChanged(userId, false);

                }
                else if(connectionStatus == ConnectionStatus.Connected){
                    coordinator.userConnectionChanged(userId, true);
                }
            }
        });

        _services.getConnectionWatcher().addConnectionWatcherListener(new ConnectionWatcherListener() {
            @Override
            public void onConnectionResume() {
                coordinator.getGameDataHelper().setOnGameDataReceivedRunnable(new OneTimeRunnable(new Runnable() {
                    @Override
                    public void run() {
                        for(Runnable runnable : onHasGameDataReceivedRunnables){
                            runnable.run();
                        }
                        onHasGameDataReceivedRunnables.clear();

                        gamePlaying = true;
                    }
                }));
            }

            @Override
            public void onConnectionHalt() {
                gamePlaying = false;
            }
        });

        _services.getGamingKit().addListener(getClassTag(), new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int code, String msg, String senderId) {
                if(code == UpdateRoomMatesCode.LOCK_PROPERTY){
                    onLockUpdateScorePropertyResult(msg);
                }
                else {
                    updateReceived(code, msg, senderId);
                }
            }

            @Override
            public void onUpdateRoomMatesReceived(byte identifier, byte[] data, String senderId) {

            }
        });


        safeThread = Threadings.countDown(60, 1000, new RunnableArgs<Integer>() {
            @Override
            public void run() {
                _scene.setRemainingTime(this.getFirstArg());
            }
        });
    }

    /////////////////////////////////////////////////////////////////
    //exiting sandbox
    //////////////////////////////////////////////////////////////////

    public void exitSandbox(){
        if(!exiting){
            _services.getBroadcaster().broadcast(BroadcastEvent.DEVICE_ORIENTATION, 0);
            _services.getChat().hideChat();
            _services.getChat().setMode(1);
            _services.getChat().newMessage(new ChatMessage(_texts.gameEnded(),
                    ChatMessage.FromType.SYSTEM, null, ""));
            _screen.switchToPTScreen();

            exiting = true;
            //gameStarted variable for failed loading case
            if(room.getGame().getLeaderboardTypeEnum() != LeaderboardType.None && gameStarted){
                endGameData.setEndGameResult(coordinator.getEndGameResult());
                _screen.toScene(leaderboardLogic, SceneEnum.END_GAME_LEADER_BOARD);
            }
            else{
                _screen.back();
                _services.getSoundsPlayer().playThemeMusic();
            }
        }
    }


    @Override
    public void dispose() {
        super.dispose();
        if(coordinator != null){
            if(coordinator.getGameEntrance() != null) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        coordinator.getGameEntrance().dispose();
                    }
                });
            }
            coordinator.dispose();
        }

        gameLoadStateMonitor.dispose();
        connectionsController.dispose();

        _services.getBroadcaster().broadcast(BroadcastEvent.DEVICE_ORIENTATION, 0);
        _services.getConnectionWatcher().gameEnded();
        _screen.switchToPTScreen();
        _services.getChat().setMode(1);

        Threadings.setContinuousRenderLock(false);
        if(safeThread != null) safeThread.kill();
    }


    @Override
    public SceneAbstract getScene() {
        return _scene;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // called from in game
    ////////////////////////////////////////////////
    @Override
    public void useConfirm(String msg, final Runnable yesRunnable, final Runnable noRunnable) {
        Confirm.Type type = Confirm.Type.YESNO;
        if(noRunnable == null){
            type = Confirm.Type.YES;
        }
        String text = _texts.getSpecialText(msg);
        if(text == null) text = msg;

        _confirm.show(text, type, new ConfirmResultListener() {
            @Override
            public void onResult(Result result) {
                if(result == Result.YES){
                    if(yesRunnable != null) yesRunnable.run();
                }
                else {
                    if(noRunnable != null) noRunnable.run();
                }
            }
        });
    }

    @Override
    public void userAbandoned(final String userId) {
        connectionsController.sendUserAbandoned(userId);
    }

    @Override
    public void endGame() {
        exitSandbox();
    }

    @Override
    public void sendUpdate(RoomUpdateType updateType, String msg) {
        _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.roomUpdateTypeToUpdateRoomMatesCode(updateType), msg);
    }

    @Override
    public void sendPrivateUpdate(RoomUpdateType updateType, String toUserId, String msg) {
        _services.getGamingKit().privateUpdateRoomMates(toUserId, UpdateRoomMatesCode.roomUpdateTypeToUpdateRoomMatesCode(updateType), msg);
    }

    @Override       //everyone will call, but abandoner cannot update score and room
    public void finalizing(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers, boolean abandoned) {
        if(!abandoned){
            this.winners = winners;
            this.losers = losers;
            if(room.getHost().getUserId().equals(_services.getProfile().getUserId())){
                _services.getDatabase().updateRoomPlayingAndOpenState(room, false, null, null);
            }

            if(room.getGame().hasLeaderboard()){
                _services.getGamingKit().lockProperty(room.getId() + "_" + room.getRoundCounter(), "1");
            }
        }

        connectionsController.dispose();
        _services.getConnectionWatcher().gameEnded();
        connectionsController.updateMyPlayingState(false, false);
    }

    public void onLockUpdateScorePropertyResult(String result){
        if(result.equals("0")){     //lock success
            _services.getRestfulApi().updateScores(winners, losers, room, _services.getProfile(), null);
        }
    }

    @Override
    public void vibrate(double periodInMili) {
        _services.getBroadcaster().broadcast(BroadcastEvent.VIBRATE_DEVICE, periodInMili);
    }

    @Override
    public void gameFailed() {
        _notification.important(_texts.notificationGameFailed());
        exitSandbox();
    }

}
