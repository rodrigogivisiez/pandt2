package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.LockPropertyListener;
import com.mygdx.potatoandtomato.absintflis.scenes.ConnectionsControllerListener;
import com.mygdx.potatoandtomato.absintflis.scenes.GameLoadStateMonitorListener;
import com.mygdx.potatoandtomato.enums.*;
import com.mygdx.potatoandtomato.helpers.Flurry;
import com.potatoandtomato.common.absints.CoinListener;
import com.mygdx.potatoandtomato.absintflis.services.ConnectionWatcherListener;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.services.Confirm;
import com.mygdx.potatoandtomato.services.Notification;
import com.mygdx.potatoandtomato.scenes.leaderboard_scene.EndGameLeaderBoardLogic;
import com.potatoandtomato.common.enums.ConfirmMsgType;
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
        _services.getConfirm().close(ConfirmIdentifier.BackScreen);

        setListenersAndThreads();

        HashMap<String, String> map = new HashMap();
        map.put("gameName", room.getGame().getName());
        map.put("totalPlayers", String.valueOf(room.getTotalPlayersCount()));
        map.put("beforeMsgSent", String.valueOf(_services.getGamingKit().getMsgSentCount()));
        Flurry.log(FlurryEvent.GameSession, map);
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
                exitSandbox(true);
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
                                                            room.getUserTeam(_services.getProfile().getUserId()).getPlayersSortedByIds());
        }
    }

    public void gameStart(){
        if(gameStarted) return;

        final Runnable startGameRunnable = new Runnable() {
            @Override
            public void run() {
                if(isDisposing()) return;

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

                _services.getSoundsPlayer().stopMusic(Sounds.Name.THEME_MUSIC);
                //for multitask still play theme music bug fix
                Threadings.delay(3000, new Runnable() {
                    @Override
                    public void run() {
                        _services.getSoundsPlayer().stopMusic(Sounds.Name.THEME_MUSIC);
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
        Flurry.log(FlurryEvent.LoadGameFailed);

        failed = true;

        _services.getChat().newMessage(new ChatMessage(_texts.chatMsgLoadGameFailed(),
                                ChatMessage.FromType.IMPORTANT, null, ""));

        Threadings.delay(2000, new Runnable() {
            @Override
            public void run() {
                exitSandbox(false);
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
                if(!isContinue){
                    _services.getCoins().setCoinListener(new CoinListener() {
                        @Override
                        public void onDeductCoinsDone() {
                            gameStart();
                        }
                    });

                    _services.getCoins().startDeductCoins();
                }
                else{
                    gameStart();
                }
            }

            @Override
            public void onPlayerReady(Player player) {
                setUserTableDesign(player.getUserId(), true, false);
            }

            @Override
            public void onFailed(Player failedPlayers) {
                if(failedPlayers != null){
                    setUserTableDesign(failedPlayers.getUserId(), false, true);
                }
                failLoad(failedPlayers);
            }

        });

        connectionsController.setConnectionsControllerListener(new ConnectionsControllerListener() {
            @Override
            public void userConnectionChanged(final String userId, final GameConnectionStatus gameConnectionStatus) {
                if(!gameStarted){
                    onGameStartedRunnables.add(new Runnable() {
                        @Override
                        public void run() {
                            userConnectionChanged(userId, gameConnectionStatus);
                        }
                    });
                    return;
                }


                if(gameConnectionStatus == GameConnectionStatus.Abandoned){
                    coordinator.userAbandon(userId);
                    if(userId.equals(_services.getProfile().getUserId())){
                        coordinator.finalizeAndEndGame(null, null, true);
                    }
                    _services.getCoins().onUserDisconnected(userId);
                }
                else if(gameConnectionStatus == GameConnectionStatus.Disconnected){
                    coordinator.userConnectionChanged(userId, false);
                    _services.getCoins().onUserDisconnected(userId);
                }
                else if(gameConnectionStatus == GameConnectionStatus.Connected){
                    coordinator.userConnectionChanged(userId, true);
                }
            }
        });

        _services.getConnectionWatcher().addConnectionWatcherListener(getClassTag(), new ConnectionWatcherListener() {
            @Override
            public void onConnectionResume() {
                if(gameStarted){
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
            }

            @Override
            public void onConnectionHalt() {
                gamePlaying = false;
                if(!gameStarted){
                    setUserTableDesign(_services.getProfile().getUserId(), true, true);
                    failLoad(room.getPlayerByUserId(_services.getProfile().getUserId()));
                }
            }
        });

        _services.getGamingKit().addListener(getClassTag(), new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int code, String msg, String senderId) {
                updateReceived(code, msg, senderId);
            }

            @Override
            public void onUpdateRoomMatesReceived(byte identifier, byte[] data, String senderId) {

            }
        });

        _services.getGamingKit().addListener(getClassTag(), new LockPropertyListener(room.getId() + "_" + room.getRoundCounter()) {
            @Override
            public void onLockSucceed() {
                onLockUpdateScorePropertySuccess();
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

    public void exitSandbox(boolean isAbandon){
        if(!exiting){
            HashMap<String, String> map = new HashMap();
            map.put("abandoned", isAbandon ? "yes" : "no");
            map.put("afterMsgSent", String.valueOf(_services.getGamingKit().getMsgSentCount()));
            Flurry.log(FlurryEvent.EndGame, map);

            _services.getBroadcaster().broadcast(BroadcastEvent.DEVICE_ORIENTATION, 0);
            _services.getChat().hideChat();
            _services.getChat().setMode(1);
            _services.getChat().newMessage(new ChatMessage(_texts.chatMsgGameEnded(),
                    ChatMessage.FromType.SYSTEM, null, ""));
            _screen.switchToPTScreen();

            exiting = true;
            //gameStarted variable for failed loading case
            if(gameStarted && !isAbandon){
                connectionsController.sendMeLeftGame();
            }

            if(room.getGame().getLeaderboardTypeEnum() != LeaderboardType.None && gameStarted){
                endGameData.setEndGameResult(coordinator.getEndGameResult());
                _screen.toScene(leaderboardLogic, SceneEnum.END_GAME_LEADER_BOARD);
            }
            else{
                _screen.back();
                _services.getSoundsPlayer().playMusic(Sounds.Name.THEME_MUSIC);
            }
        }
    }

    @Override
    public boolean disposeEarly() {
        if(super.disposeEarly()){
            gameLoadStateMonitor.disposeGameCoordinator();
            gameLoadStateMonitor.dispose();
            connectionsController.dispose();
            _services.getConnectionWatcher().clearConnectionWatcherListenerByClassTag(getClassTag());
            if(safeThread != null) safeThread.kill();
        }
        return true;
    }

    @Override
    public boolean dispose() {
        if(super.dispose()){
            Flurry.logTimeEnd(FlurryEvent.GameSession);

            _services.getBroadcaster().broadcast(BroadcastEvent.DEVICE_ORIENTATION, 0);
            _screen.switchToPTScreen();
            _services.getChat().setMode(1);
        }
        return true;
    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // called from in game
    ////////////////////////////////////////////////
    @Override
    public void useConfirm(ConfirmMsgType msgType, Runnable yesRunnable, Runnable noRunnable) {
        String text = "";
        if(msgType == ConfirmMsgType.AbandonGameConsLoseStreak){
            text = _texts.confirmAbandonLoseStreak();
        }
        else if(msgType == ConfirmMsgType.AbandonGameNoCons){
            text = _texts.confirmAbandonNoCons();
        }
        else if(msgType == ConfirmMsgType.CannotAbandon){
            text = _texts.confirmCannotAbandon();
        }
        useConfirm(text, yesRunnable, noRunnable);
    }


    @Override
    public void useConfirm(String msg, final Runnable yesRunnable, final Runnable noRunnable) {
        Confirm.Type type = Confirm.Type.YESNO;
        if(noRunnable == null){
            type = Confirm.Type.YES;
        }

        _confirm.show(ConfirmIdentifier.GameSandBox, msg, type, new ConfirmResultListener() {
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
    public void useNotification(String msg) {
        _notification.info(msg);
    }

    @Override
    public void userAbandoned(final String userId) {
        connectionsController.sendUserAbandoned(userId);
    }

    @Override
    public void endGame(boolean isAbandon) {
        exitSandbox(isAbandon);
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

        connectionsController.updateMyPlayingState(false, abandoned);
    }

    public void onLockUpdateScorePropertySuccess(){
        _services.getRestfulApi().updateScores(winners, losers, room, _services.getProfile(), null);
    }

    @Override
    public void vibrate(double periodInMili) {
        _services.getBroadcaster().broadcast(BroadcastEvent.VIBRATE_DEVICE, periodInMili);
    }

    @Override
    public void gameFailed(String msg) {
        _notification.important(msg);
        if(coordinator.getAllConnectedPlayers().size() == 1){       //only me
            _services.getDatabase().updateRoomPlayingAndOpenState(room, false, null, null);
        }
        exitSandbox(false);
    }

}
