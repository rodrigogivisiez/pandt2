package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.LeaderboardType;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.services.Confirm;
import com.mygdx.potatoandtomato.services.Notification;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.scenes.leaderboard_scene.EndGameLeaderBoardLogic;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.utils.JsonObj;
import com.potatoandtomato.common.utils.Threadings;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.absints.IGameSandBox;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.InGameUpdateMessage;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.ThreadsPool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public class GameSandboxLogic extends LogicAbstract implements IGameSandBox {

    IGameSandBox _me;
    GameSandboxScene _scene;
    Room _room;
    ArrayList<String> _readyUserIds;
    boolean _gameCanStart;
    GameCoordinator _coordinator;
    Notification _notification;
    boolean _isContinue;
    boolean _isReady;
    boolean _gameStarted;
    boolean _failed;
    boolean _exiting;
    ArrayList<String> _monitorRetrievedUserId;
    EndGameData _endGameData;
    EndGameLeaderBoardLogic _leaderboardLogic;
    HashMap<Team, ArrayList<ScoreDetails>> _winners;
    ArrayList<Team> _losers;

    public GameSandboxLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        setSaveToStack(false);
        _failed = false;
        _me = this;
        _notification = services.getNotification();
        _scene = new GameSandboxScene(_services, _screen);
        _readyUserIds = new ArrayList<String>();
        _monitorRetrievedUserId = new ArrayList<String>();
        _room = (Room) objs[0];
        _isContinue = (Boolean) objs[1];
        initiateUserReady();
        initiateEndGameEssential();
        Threadings.setContinuousRenderLock(true);
    }

    @Override
    public void onQuit(final OnQuitListener listener) {
        listener.onResult(_gameStarted || _failed ? OnQuitListener.Result.YES : OnQuitListener.Result.NO);
    }

    @Override
    public void onInit() {
        super.onInit();

        _services.getPreferences().setGameAbbr(_room.getGame().getAbbr());

        _services.getChat().hideChat();

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

        _services.getDatabase().monitorRoomById(_room.getId(), getClassTag(), new DatabaseListener<Room>(Room.class) {
            @Override
            public void onCallback(Room obj, Status st) {
                ArrayList<RoomUser> leftUsers = _room.getJustLeftUsers(obj);
                if(leftUsers.size() > 0){
                    if(!_gameCanStart){
                        userLeftRoom(leftUsers.get(0).getProfile().getUserId());
                    }
                }
            }
        });

        subscribeBroadcast(BroadcastEvent.LOAD_GAME_RESPONSE, new BroadcastListener<GameCoordinator>() {
            @Override
            public void onCallback(GameCoordinator obj, Status st) {
                if (st == Status.SUCCESS) {
                    _coordinator = obj; //coordinator will call gameLoaded of sandbox
                } else {
                    _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.LOAD_FAILED, "");
                }
            }
        });

        startHostUserReadyMonitor();
        startTimeoutThread();
    }

    @Override
    public void onShown() {
        super.onShown();
        publishBroadcast(BroadcastEvent.LOAD_GAME_REQUEST, new GameCoordinator(_room.getGame().getFullLocalJarPath(),
                _room.getGame().getLocalAssetsPath(), _room.getGame().getBasePath(), _room.getTeams(),
                Positions.getWidth(), Positions.getHeight(), _screen.getGame(), _screen.getGame().getSpriteBatch(),
                _services.getProfile().getUserId(), _me, _services.getDatabase().getGameBelongDatabase(_room.getGame().getAbbr()),
                _room.getId(), _services.getSoundsPlayer(), getBroadcaster(), _services.getDownloader(), _services.getTutorials(),
                _services.getPreferences(), Global.LEADERBOARD_COUNT));
    }

    private void initiateUserReady(){
        if(_isContinue){
            Profile myProfile = _services.getProfile();
            setUserTable(myProfile.getUserId(), false, false);
        }
        else{
            for(RoomUser roomUser : _room.getRoomUsersMap().values()){
                ;Profile profile = roomUser.getProfile();
                setUserTable(profile.getUserId(), false, false);
            }
        }
    }

    private void initiateEndGameEssential(){
        _endGameData = new EndGameData(_room, _services.getProfile().getUserId());
        if(_room.getGame().hasLeaderboard()){
            _leaderboardLogic = new EndGameLeaderBoardLogic(_screen, _services, _endGameData,
                                                            _room.getUserTeam(_services.getProfile().getUserId()).getPlayers());
        }
    }

    private void startHostUserReadyMonitor(){
        if(!_isContinue){
            if(_room.getHost().equals(_services.getProfile())){

                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            Threadings.sleep(3000);
                            if(_failed) return;

                            if(_readyUserIds.size() < _room.getRoomUsersCount() && !_gameCanStart){
                                askForUserIsReady();
                            }
                            else{
                                break;
                            }
                        }
                    }
                });
            }
        }
    }

    private void startTimeoutThread(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int i = 60;
                while (true){
                    if(_failed) return;

                    i--;
                    setRemainingTime(i);

                    if(i == 0 && !_gameCanStart){
                        failLoad(null);
                    }
                    Threadings.sleep(1000);

                }
            }
        });
    }

    public void userIsReady(String userId){
        if(!_readyUserIds.contains(userId)){
            _readyUserIds.add(userId);
            setUserTable(userId, true, false);
        }
        if(_readyUserIds.size() >= _room.getRoomUsersCount() && _services.getProfile().equals(_room.getHost())){
            _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.ALL_PLAYERS_LOADED_GAME_SUCCESS, "");  //only host can send
        }
    }

    public void gameStart(){

        if(_gameCanStart) return;

        _gameCanStart = true;

        final Runnable dbMonitor = new Runnable() {
            @Override
            public void run() {
                _services.getProfile().setUserPlayingState(new UserPlayingState(_room.getId(), true, _room.getRoundCounter()));
                _services.getDatabase().updateProfile(_services.getProfile(), new DatabaseListener() {
                    @Override
                    public void onCallback(Object obj, Status st) {
                        for(final RoomUser roomUser : _room.getOriginalRoomUsers()){
                            String userId = roomUser.getProfile().getUserId();
                            _services.getDatabase().monitorProfileByUserId(userId, getClassTag(), new DatabaseListener<Profile>(Profile.class) {
                                @Override
                                public void onCallback(final Profile obj, Status st) {
                                    if (st == Status.SUCCESS && obj != null) {
                                        if(!_monitorRetrievedUserId.contains(obj.getUserId())){
                                            _monitorRetrievedUserId.add(obj.getUserId());
                                            return;
                                        }

                                        final UserPlayingState userPlayingState = obj.getUserPlayingState();
                                        if (userPlayingState != null) {
                                            if (userPlayingState.getRoomId().equals(_room.getId())
                                                            && userPlayingState.getRoundCounter() == _room.getRoundCounter()) {
                                                Threadings.postRunnable(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (userPlayingState.getAbandon()) {
                                                            //user abandoned
                                                            _services.getChat().newMessage(new ChatMessage(String.format(_texts.notificationAbandon(),
                                                                    obj.getDisplayName(0)), ChatMessage.FromType.IMPORTANT, null, ""));
                                                            _notification.important(String.format(_texts.notificationAbandon(), obj.getDisplayName(15)));
                                                            _coordinator.userAbandon(obj.getUserId());
                                                        } else if (userPlayingState.getConnected()) {
                                                            //user connected back
                                                            _notification.info(String.format(_texts.notificationConnected(), obj.getDisplayName(15)));
                                                            _coordinator.userConnectionChanged(obj.getUserId(), true);
                                                        } else if (!userPlayingState.getConnected()) {
                                                            //user disconnected
                                                            _notification.important(String.format(_texts.notificationDisconnected(), obj.getDisplayName(15)));
                                                            _coordinator.userConnectionChanged(obj.getUserId(), false);
                                                        }
                                                    }
                                                });
                                            }
                                        }

                                    }
                                }
                            });
                        }
                    }
                });
            }
        };

        for(RoomUser roomUser : _room.getRoomUsersMap().values()){
            setUserTable(roomUser.getProfile().getUserId(), true, false);
        }

        Threadings.delay(500, new Runnable() {
            @Override
            public void run() {
                _screen.switchToGameScreen();
                if(!_isContinue){
                    _coordinator.getGameEntrance().init();
                }
                else{
                    _coordinator.getGameEntrance().onContinue();
                }
                _scene.clearRoot();
                _services.getChat().setMode(2);
                _services.getChat().resetChat();
                _services.getChat().showChat();
                if(_coordinator.isLandscape()){
                    _services.getBroadcaster().broadcast(BroadcastEvent.DEVICE_ORIENTATION, 1);
                }
                _gameStarted = true;
                dbMonitor.run();


                _services.getSoundsPlayer().stopThemeMusic();
                //for multitask still play theme music bug fix
                Threadings.delay(3000, new Runnable() {
                    @Override
                    public void run() {
                        _services.getSoundsPlayer().stopThemeMusic();
                    }
                });

            }
        });


    }

    public void userLeftRoom(String userId){
        _failed = true;
        _services.getChat().newMessage(new ChatMessage(_texts.playerLeftCauseGameCancel(),
                ChatMessage.FromType.IMPORTANT, null, ""));

        setUserTable(userId, false, true);

        Threadings.delay(2000, new Runnable() {
            @Override
            public void run() {
                exitSandbox();
            }
        });

    }

    public void failLoad(String userId){
        _failed = true;

        if(userId == null && getNotReadyUsers().size() > 0){
            userId = getNotReadyUsers().get(0).getUserId();
        }

        if(userId != null){
            _services.getChat().newMessage(new ChatMessage(_texts.loadGameFailed(),
                    ChatMessage.FromType.IMPORTANT, null, ""));

            setUserTable(userId, false, true);

        }

        Threadings.delay(2000, new Runnable() {
            @Override
            public void run() {
                if(!_isContinue){
                    exitSandbox();
                }
                else{

                }
            }
        });
    }


    @Override
    public void onGameLoaded() {
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (_coordinator == null || (_coordinator.getGameEntrance() == null && !_failed)){
                    Threadings.sleep(500);
                }
                if(!_failed){
                    loadLeaderboardRecordToTeam(new Runnable() {
                        @Override
                        public void run() {
                            _isReady = true;
                            if(!_isContinue){
                                _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.USER_IS_READY, "");
                            }
                            else{
                                gameStart();
                            }
                        }
                    });
                }
            }
        });
    }

    private void loadLeaderboardRecordToTeam(final Runnable onFinish){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                ThreadsPool threadsPool = new ThreadsPool();
                for(final Team team : _room.getTeams()){
                    final Threadings.ThreadFragment threadFragment = new Threadings.ThreadFragment();
                    _services.getDatabase().getTeamHighestLeaderBoardRecordAndStreak(_room.getGame(), team.getPlayersUserIds(),
                            new DatabaseListener<LeaderboardRecord>(LeaderboardRecord.class) {
                                @Override
                                public void onCallback(LeaderboardRecord record, Status st) {
                                    if (st == Status.SUCCESS && record != null && team.matchedUsers(record.getUserIds())) {
                                        team.setLeaderboardRecord(record);
                                    }
                                    threadFragment.setFinished(true);
                                }
                            });
                    threadsPool.addFragment(threadFragment);
                }

                //the purpose of above function is to complement with this part, we cant use
                //this function only to get leaderboard and streak because user might not be in the leaderboard
                final Threadings.ThreadFragment allLeaderboardFragment = new Threadings.ThreadFragment();
                _services.getDatabase().getLeaderBoardAndStreak(_room.getGame(), Global.LEADERBOARD_COUNT, new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
                    @Override
                    public void onCallback(ArrayList<LeaderboardRecord> records, Status st) {
                        if(st == Status.SUCCESS){
                            for(int i = records.size() - 1; i >= 0 ;i--){
                                _coordinator.getGameLeaderboardRecords().add(0, records.get(i));
                                for(Team team : _room.getTeams()){
                                    if(team.matchedUsers(records.get(i).getUserIds())){
                                        team.setRank(i + 1);
                                    }
                                }
                            }
                        }
                        allLeaderboardFragment.setFinished(true);
                    }
                });
                threadsPool.addFragment(allLeaderboardFragment);

                while (!threadsPool.allFinished()){
                    Threadings.sleep(300);
                }
                onFinish.run();
            }
        });

    }

    public void updateReceived(int code, String msg, String senderId){
        if(code == UpdateRoomMatesCode.USER_IS_READY){
            userIsReady(senderId);
        }
        else if(code == UpdateRoomMatesCode.ASK_FOR_USER_READY){
            if(msg.equals(_services.getProfile().getUserId()) && _isReady){
                _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.USER_IS_READY, "");    //resend again
            }
            if(msg.equals(_services.getProfile().getUserId()) && _failed){
                _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.LOAD_FAILED, "");    //resend again
            }
        }
        else if(code == UpdateRoomMatesCode.IN_GAME_UPDATE){
            publishBroadcast(BroadcastEvent.INGAME_UPDATE_RESPONSE, new InGameUpdateMessage(senderId, msg));
        }
        else if(code == UpdateRoomMatesCode.ALL_PLAYERS_LOADED_GAME_SUCCESS){
            gameStart();
        }
        else if(code == UpdateRoomMatesCode.LOAD_FAILED){
            failLoad(senderId);
        }
        else if(code == UpdateRoomMatesCode.KICK_USER){
            JsonObj jsonObj = new JsonObj(msg);
            String kickedUserId = jsonObj.getString("userId");
            if(kickedUserId.equals(_services.getProfile().getUserId())){
                _notification.important(_texts.notificationKicked());
                exitSandbox();
            }
        }
    }

    public void askForUserIsReady(){
        ArrayList<Profile> profiles = getNotReadyUsers();
        for(Profile p : profiles){
            _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.ASK_FOR_USER_READY, p.getUserId());
        }
    }

    public ArrayList<Profile> getNotReadyUsers(){
        ArrayList<Profile> profiles = new ArrayList<Profile>();
        for(RoomUser roomUser : _room.getRoomUsersMap().values()){
            if(!_readyUserIds.contains(roomUser.getProfile().getUserId())){
                profiles.add(roomUser.getProfile());
            }
        }
        return profiles;
    }

    public void exitSandbox(){
        _screen.switchToPTScreen();
        _services.getBroadcaster().broadcast(BroadcastEvent.DEVICE_ORIENTATION, 0);
        _services.getChat().setMode(1);
        redirectExitedSandbox();
    }

    private void redirectExitedSandbox(){
        if(!_exiting){
            _exiting = true;
            _services.getChat().newMessage(new ChatMessage(_texts.gameEnded(),
                    ChatMessage.FromType.SYSTEM, null, ""));
            //_gameStarted variable for failed loading case
            if(_room.getGame().getLeaderboardTypeEnum() != LeaderboardType.None && _gameStarted){
                _endGameData.setEndGameResult(_coordinator.getEndGameResult());
                _services.getChat().hideChat();
                _screen.toScene(_leaderboardLogic, SceneEnum.END_GAME_LEADER_BOARD);
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
        _screen.switchToPTScreen();
        if(_coordinator != null){
            if(_coordinator.getGameEntrance() != null) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _coordinator.getGameEntrance().dispose();
                    }
                });
            }
            _services.getBroadcaster().broadcast(BroadcastEvent.DEVICE_ORIENTATION, 0);
            _coordinator.dispose();
        }
        Threadings.setContinuousRenderLock(false);
    }

    public void setUserTable(final String userId, final boolean isReady, final boolean isFailed){
        _scene.setUser(userId, _room.getRoomUserByUserId(userId).getProfile().getDisplayName(15),
                isReady, isFailed, _room.getUserColorByUserId(userId));
    }

    public void setRemainingTime(final int sec){
        _scene.setRemainingTime(sec);
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
        _coordinator.userConnectionChanged(_services.getProfile().getUserId(), false);

        if(userId.equals(_services.getProfile().getUserId())){
            _services.getProfile().getUserPlayingState().setAbandon(true);
            _services.getProfile().getUserPlayingState().setConnected(false);
            _services.getDatabase().updateProfile(_services.getProfile(), null);
        }

    }

    @Override
    public void endGame() {
        exitSandbox();
    }

    @Override
    public void inGameUpdateRequest(String msg) {
        _services.getGamingKit().updateRoomMates(UpdateRoomMatesCode.IN_GAME_UPDATE, msg);
    }


    @Override       //everyone will call this
    public void updateScores(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers) {
        this._winners = winners;
        this._losers = losers;
        _services.getDatabase().updateRoomPlayingState(_room, false, null);

        if(_room.getGame().hasLeaderboard()){
            _services.getGamingKit().lockProperty(_room.getId() + "_" + _room.getRoundCounter(), "1");
        }
    }

    public void onLockUpdateScorePropertyResult(String result){
        if(result.equals("0")){
            _services.getRestfulApi().updateScores(_winners, _losers, _room, _services.getProfile(), null);
        }
    }

}
